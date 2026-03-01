package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.AssignFloorDto;

import java.util.List;

public interface InspectorFloorService {

  void assignFloors(Long inspectorId, AssignFloorDto dto);

  List<Integer> getFloors(Long inspectorId);
}
