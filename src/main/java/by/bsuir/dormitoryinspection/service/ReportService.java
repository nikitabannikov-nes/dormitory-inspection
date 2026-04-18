package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.ReportRequestDto;

public interface ReportService {

  byte[] generateMonthlyReport(ReportRequestDto request);
}
