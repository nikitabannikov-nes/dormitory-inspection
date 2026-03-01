package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.InspectionCreateDto;
import by.bsuir.dormitoryinspection.dto.response.InspectionDto;

import java.util.List;

public interface InspectionService {

  InspectionDto create(InspectionCreateDto dto, String inspectorUsername);

  List<InspectionDto> getByBlock(Long blockId);

  List<InspectionDto> getMyInspections(String inspectorUsername);

  List<InspectionDto> getAll();
}
