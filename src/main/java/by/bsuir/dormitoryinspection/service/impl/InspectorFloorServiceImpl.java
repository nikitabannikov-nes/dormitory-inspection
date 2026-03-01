package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.AssignFloorDto;
import by.bsuir.dormitoryinspection.entity.InspectorFloor;
import by.bsuir.dormitoryinspection.entity.InspectorFloorId;
import by.bsuir.dormitoryinspection.entity.User;
import by.bsuir.dormitoryinspection.enums.Role;
import by.bsuir.dormitoryinspection.repository.InspectorFloorRepository;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.service.InspectorFloorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectorFloorServiceImpl implements InspectorFloorService {

  private final InspectorFloorRepository inspectorFloorRepository;
  public final UserRepository userRepository;

  @Override
  @Transactional
  public void assignFloors(Long inspectorId, AssignFloorDto dto) {
    User inspector = userRepository.findById(inspectorId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + inspectorId));

    if (inspector.getRole() == Role.USER) {
      throw new IllegalArgumentException(
              "Cannot assign floors to USER role. Only INSPECTOR or ADMIN allowed.");
    }

    inspectorFloorRepository.deleteAllByInspectorId(inspectorId);

    List<InspectorFloor> floors = dto.getFloorNumbers()
            .stream()
            .map(floorNumber -> {
              InspectorFloor floor = new InspectorFloor();
              floor.setId(new InspectorFloorId(inspectorId, floorNumber));
              floor.setInspector(inspector);
              return floor;
            })
            .toList();

    inspectorFloorRepository.saveAll(floors);
  }

  @Override
  public List<Integer> getFloors(Long inspectorId) {
    if (!userRepository.existsById(inspectorId)) {
      throw new EntityNotFoundException("User not found: " + inspectorId);
    }
    return inspectorFloorRepository.findAllByInspectorId(inspectorId)
            .stream()
            .map(InspectorFloor::getFloorNumber)
            .toList();
  }
}
