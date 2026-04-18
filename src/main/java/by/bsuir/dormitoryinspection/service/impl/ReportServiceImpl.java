package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.ReportRequestDto;
import by.bsuir.dormitoryinspection.entity.Inspection;
import by.bsuir.dormitoryinspection.repository.InspectionRepository;
import by.bsuir.dormitoryinspection.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

  private static final int DORMITORY_NUMBER = 4;

  private static final Map<Integer, String> MONTH_PREPOSITIONAL = Map.ofEntries(
      Map.entry(1, "январе"),   Map.entry(2, "феврале"),  Map.entry(3, "марте"),
      Map.entry(4, "апреле"),   Map.entry(5, "мае"),      Map.entry(6, "июне"),
          Map.entry(9, "сентябре"),
      Map.entry(10, "октябре"), Map.entry(11, "ноябре"),  Map.entry(12, "декабре")
  );

  private static final Map<Integer, String> MONTH_NOMINATIVE = Map.ofEntries(
      Map.entry(1, "январь"),   Map.entry(2, "февраль"),  Map.entry(3, "март"),
      Map.entry(4, "апрель"),   Map.entry(5, "май"),      Map.entry(6, "июнь"),
          Map.entry(9, "сентябрь"),
      Map.entry(10, "октябрь"), Map.entry(11, "ноябрь"), Map.entry(12, "декабрь")
  );

  private final InspectionRepository inspectionRepository;

  @Override
  public byte[] generateMonthlyReport(ReportRequestDto request) {
    int year  = LocalDate.now().getYear();
    int month = request.getMonth();

    List<Inspection> inspections = inspectionRepository.findAllByMonthAndYear(month, year);
    log.info("Report: month={}, year={}, found {} inspections", month, year, inspections.size());

    try (XWPFDocument doc = new XWPFDocument();
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      setPageMargins(doc);
      // Шапка: 16pt, выравнивание по правому краю
      addRightParagraph(doc, "Отчет", 16, true);
      addAcademicYearSubtitle(doc, month, year);
      addRightParagraph(doc, "Жилищно-бытовая комиссия общежития №" + DORMITORY_NUMBER, 16, false);
      addRightParagraph(doc, "Председатель: " + request.getChairmanFio(), 16, false);
      addEmptyParagraph(doc);
      addPerformedWork(doc, month, request.getPerformedWork());
      addEmptyParagraph(doc);
      addInspectionSection(doc, inspections);

      doc.write(out);
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Ошибка при генерации отчёта", e);
    }
  }

  // ─── Разделы документа ───────────────────────────────────────────────────

  private void addAcademicYearSubtitle(XWPFDocument doc, int month, int year) {
    int start = month >= 9 ? year : year - 1;
    addRightParagraph(doc,
        "О проделанной работе за " + MONTH_NOMINATIVE.getOrDefault(month, "") +
        " " + start + "-" + (start + 1) + " учебного года", 16, false);
  }

  private void addPerformedWork(XWPFDocument doc, int month, List<String> items) {
    // Заголовок: 14pt, жирный, по левому краю
    XWPFParagraph header = doc.createParagraph();
    header.setAlignment(ParagraphAlignment.LEFT);
    setSingleSpacing(header);
    XWPFRun r = header.createRun();
    r.setBold(true);
    r.setFontSize(14);
    r.setFontFamily("Times New Roman");
    r.setText("Выполненная работа в " + MONTH_PREPOSITIONAL.getOrDefault(month, "") + ":");

    if (items == null) return;

    // Создаём Word-список с тире через нумерацию
    BigInteger numId = createDashListNumId(doc);

    for (String item : items) {
      if (item == null || item.isBlank()) continue;
      XWPFParagraph p = doc.createParagraph();
      p.setAlignment(ParagraphAlignment.LEFT);
      setSingleSpacing(p);
      p.setNumID(numId);
      p.setNumILvl(BigInteger.ZERO);
      XWPFRun run = p.createRun();
      run.setFontSize(14);
      run.setFontFamily("Times New Roman");
      run.setText(item.trim());
    }
  }

  /**
   * Регистрирует в документе абстрактный список с тире и возвращает numId.
   * Вызывается один раз на документ.
   */
  private BigInteger createDashListNumId(XWPFDocument doc) {
    XWPFNumbering numbering = doc.getNumbering() != null
        ? doc.getNumbering() : doc.createNumbering();

    CTAbstractNum ctAbstractNum = CTAbstractNum.Factory.newInstance();
    ctAbstractNum.setAbstractNumId(BigInteger.valueOf(1));
    CTLvl lvl = ctAbstractNum.addNewLvl();
    lvl.setIlvl(BigInteger.ZERO);
    lvl.addNewStart().setVal(BigInteger.ONE);
    lvl.addNewNumFmt().setVal(STNumberFormat.BULLET);
    lvl.addNewLvlText().setVal("-");
    lvl.addNewLvlJc().setVal(STJc.LEFT);
    CTPPrGeneral lvlPPr = lvl.addNewPPr();
    CTInd ind = lvlPPr.addNewInd();
    ind.setLeft(BigInteger.valueOf(720));
    ind.setHanging(BigInteger.valueOf(360));
    CTRPr lvlRPr = lvl.addNewRPr();
    lvlRPr.addNewRFonts().setAscii("Times New Roman");

    BigInteger abstractNumId = numbering.addAbstractNum(new XWPFAbstractNum(ctAbstractNum));
    return numbering.addNum(abstractNumId);
  }

  private void addInspectionSection(XWPFDocument doc, List<Inspection> inspections) {
    // "Проверки ЖБК": 16pt, по центру
    XWPFParagraph p = doc.createParagraph();
    p.setAlignment(ParagraphAlignment.CENTER);
    setSingleSpacing(p);
    XWPFRun run = p.createRun();
    run.setBold(true);
    run.setFontSize(16);
    run.setFontFamily("Times New Roman");
    run.setText("Проверки ЖБК");

    if (inspections.isEmpty()) {
      XWPFParagraph empty = doc.createParagraph();
      setSingleSpacing(empty);
      empty.createRun().setText("Данные за выбранный период отсутствуют.");
      return;
    }

    Map<Integer, List<Inspection>> byFloor = inspections.stream()
        .collect(Collectors.groupingBy(
            i -> i.getBlock().getFloor(), TreeMap::new, Collectors.toList()));

    for (Map.Entry<Integer, List<Inspection>> entry : byFloor.entrySet()) {
      addEmptyParagraph(doc);
      // Подпись этажа: 16pt, по центру
      XWPFParagraph fp = doc.createParagraph();
      fp.setAlignment(ParagraphAlignment.CENTER);
      setSingleSpacing(fp);
      XWPFRun fr = fp.createRun();
      fr.setBold(true);
      fr.setFontSize(16);
      fr.setFontFamily("Times New Roman");
      fr.setText(entry.getKey() + " этаж");
      addFloorTable(doc, entry.getValue(), entry.getKey());
    }
  }

  // ─── Таблица этажа ───────────────────────────────────────────────────────

  /**
   * Структура таблицы (8 колонок):
   *   Блок | Душ | Туалет | Коридор | Кухня | Ком.А | Ком.Б | Ср.Балл
   *
   * В строке с датой/проверяющим: колонки Коридор+Кухня объединены.
   * Для 2-го этажа в строках с оценками: колонки Ком.А+Ком.Б объединены.
   */
  private void addFloorTable(XWPFDocument doc, List<Inspection> floorInspections, int floor) {
    XWPFTable table = doc.createTable();
    applyTableBorders(table);
    setTableFullWidth(table);

    // ── Заголовок ──
    XWPFTableRow hdr = table.getRow(0);
    fillHeaderRow(hdr, new String[]{"Блок","Душ","Туалет","Коридор","Кухня","Ком. А","Ком. Б","Ср. Балл"});

    // ── Группировка по (дата, inspector_id) ──
    Map<String, List<Inspection>> bySession = new LinkedHashMap<>();
    for (Inspection i : floorInspections) {
      String key = i.getDate() + "|" + (i.getInspector() != null ? i.getInspector().getId() : 0);
      bySession.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
    }

    boolean first = true;
    for (List<Inspection> session : bySession.values()) {
      Inspection s = session.get(0);
      String monthName = MONTH_NOMINATIVE.getOrDefault(s.getDate().getMonthValue(), "");
      String inspFio   = s.getInspector() != null ? s.getInspector().getFio() : "—";
      int    day       = s.getDate().getDayOfMonth();

      // Строка с датой и проверяющим: колонки Коридор(3)+Кухня(4) объединены
      XWPFTableRow sRow = table.createRow();
      ensureCells(sRow, 8);
      setCellGray(sRow.getCell(0), first ? monthName : "");
      setCellGray(sRow.getCell(1), String.valueOf(day));
      setCellGray(sRow.getCell(2), "Проверяющий");
      setCellGray(sRow.getCell(3), inspFio);
      setCellGray(sRow.getCell(4), "");
      for (int c = 5; c < 8; c++) setCellGray(sRow.getCell(c), "");
      setGridSpan(sRow.getCell(3), 2);
      sRow.getCtRow().removeTc(4);
      first = false;

      // Строки блоков
      session.stream()
          .sorted(Comparator.comparingInt(i -> i.getBlock().getNumber()))
          .forEach(insp -> {
            XWPFTableRow dRow = table.createRow();
            ensureCells(dRow, 8);
            setCell(dRow.getCell(0), String.valueOf(insp.getBlock().getNumber()), false);
            setCell(dRow.getCell(1), fmt(insp.getShower()),  false);
            setCell(dRow.getCell(2), fmt(insp.getToilet()),  false);
            setCell(dRow.getCell(3), fmt(insp.getHall()),    false);
            setCell(dRow.getCell(4), fmt(insp.getKitchen()), false);
            setCell(dRow.getCell(5), fmt(insp.getRoomA()),   false);
            setCell(dRow.getCell(6), fmt(insp.getRoomB()),   false);
            setCell(dRow.getCell(7), calcAvg(insp),          true);
            // Только для 2-го этажа: Ком.А(5)+Ком.Б(6) объединены
            if (floor == 2) {
              setGridSpan(dRow.getCell(5), 2);
              dRow.getCtRow().removeTc(6);
            }
          });
    }
  }

  // ─── Вспомогательные: строки и ячейки ────────────────────────────────────

  private void fillHeaderRow(XWPFTableRow row, String[] labels) {
    ensureCells(row, labels.length);
    for (int i = 0; i < labels.length; i++) {
      XWPFTableCell cell = row.getCell(i);
      setBg(cell, "D9D9D9");
      writeCellRun(cell, labels[i], true, false);
    }
  }

  private void ensureCells(XWPFTableRow row, int n) {
    while (row.getTableCells().size() < n) row.addNewTableCell();
  }

  private void setCellGray(XWPFTableCell cell, String text) {
    setBg(cell, "EEEEEE");
    writeCellRun(cell, text, false, true);
  }

  private void setCell(XWPFTableCell cell, String text, boolean bold) {
    writeCellRun(cell, text, bold, false);
  }

  private void writeCellRun(XWPFTableCell cell, String text, boolean bold, boolean italic) {
    cell.setVerticalAlignment(XWPFVertAlign.CENTER);
    XWPFParagraph p = cell.getParagraphs().get(0);
    p.setAlignment(ParagraphAlignment.CENTER);
    setSingleSpacing(p);
    for (int i = p.getRuns().size() - 1; i >= 0; i--) p.removeRun(i);
    XWPFRun r = p.createRun();
    r.setBold(bold);
    r.setItalic(italic);
    r.setFontSize(12);
    r.setFontFamily("Times New Roman");
    r.setText(text != null ? text : "");
  }

  private void setBg(XWPFTableCell cell, String hex) {
    CTTcPr tcPr = cell.getCTTc().getTcPr() != null
        ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
    CTShd shd = tcPr.getShd() != null ? tcPr.getShd() : tcPr.addNewShd();
    shd.setVal(STShd.CLEAR);
    shd.setColor("auto");
    shd.setFill(hex);
  }

  private void setGridSpan(XWPFTableCell cell, int span) {
    CTTcPr tcPr = cell.getCTTc().isSetTcPr()
        ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
    CTDecimalNumber gridSpan = tcPr.isSetGridSpan()
        ? tcPr.getGridSpan() : tcPr.addNewGridSpan();
    gridSpan.setVal(BigInteger.valueOf(span));
  }

  private void applyTableBorders(XWPFTable table) {
    table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
  }

  private void setTableFullWidth(XWPFTable table) {
    CTTblPr tblPr = table.getCTTbl().getTblPr() != null
        ? table.getCTTbl().getTblPr() : table.getCTTbl().addNewTblPr();
    CTTblWidth tblW = tblPr.getTblW() != null ? tblPr.getTblW() : tblPr.addNewTblW();
    tblW.setType(STTblWidth.PCT);
    tblW.setW(BigInteger.valueOf(5000));
  }

  // ─── Вспомогательные: стили документа ────────────────────────────────────

  private void setPageMargins(XWPFDocument doc) {
    CTSectPr sect = doc.getDocument().getBody().isSetSectPr()
        ? doc.getDocument().getBody().getSectPr()
        : doc.getDocument().getBody().addNewSectPr();
    CTPageMar mar = sect.isSetPgMar() ? sect.getPgMar() : sect.addNewPgMar();
    mar.setTop(BigInteger.valueOf(1134));
    mar.setBottom(BigInteger.valueOf(1134));
    mar.setLeft(BigInteger.valueOf(1701));
    mar.setRight(BigInteger.valueOf(850));
  }

  private void addRightParagraph(XWPFDocument doc, String text, int size, boolean bold) {
    XWPFParagraph p = doc.createParagraph();
    p.setAlignment(ParagraphAlignment.RIGHT);
    setSingleSpacing(p);
    XWPFRun r = p.createRun();
    r.setBold(bold);
    r.setFontSize(size);
    r.setFontFamily("Times New Roman");
    r.setText(text);
  }

  private void addEmptyParagraph(XWPFDocument doc) {
    XWPFParagraph p = doc.createParagraph();
    setSingleSpacing(p);
  }

  private void setSingleSpacing(XWPFParagraph p) {
    CTPPr pPr = p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();
    CTSpacing spacing = pPr.isSetSpacing() ? pPr.getSpacing() : pPr.addNewSpacing();
    spacing.setLineRule(STLineSpacingRule.AUTO);
    spacing.setLine(BigInteger.valueOf(240));
    spacing.setBefore(BigInteger.ZERO);
    spacing.setAfter(BigInteger.ZERO);
  }

  // ─── Вычисления ──────────────────────────────────────────────────────────

  private String fmt(Byte score) {
    return score != null ? String.valueOf(score) : "";
  }

  private String calcAvg(Inspection i) {
    int sum = 0, cnt = 0;
    if (i.getShower()  != null) { sum += i.getShower();  cnt++; }
    if (i.getToilet()  != null) { sum += i.getToilet();  cnt++; }
    if (i.getHall()    != null) { sum += i.getHall();    cnt++; }
    if (i.getKitchen() != null) { sum += i.getKitchen(); cnt++; }
    if (i.getRoomA()   != null) { sum += i.getRoomA();   cnt++; }
    if (i.getRoomB()   != null) { sum += i.getRoomB();   cnt++; }
    if (cnt == 0) return "";
    return BigDecimal.valueOf((double) sum / cnt)
        .setScale(1, RoundingMode.HALF_UP)
        .toString().replace('.', ',');
  }
}
