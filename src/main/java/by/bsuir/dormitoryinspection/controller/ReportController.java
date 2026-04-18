package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.request.ReportRequestDto;
import by.bsuir.dormitoryinspection.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/monthly")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> generateMonthlyReport(@Valid @RequestBody ReportRequestDto request) {
    byte[] docBytes = reportService.generateMonthlyReport(request);

    String filename = "report_" + LocalDate.now().getYear() + "_" + request.getMonth() + ".docx";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    headers.setContentDisposition(
            ContentDisposition.attachment().filename(filename).build());
    headers.setContentLength(docBytes.length);

    return ResponseEntity.ok().headers(headers).body(docBytes);
  }
}
