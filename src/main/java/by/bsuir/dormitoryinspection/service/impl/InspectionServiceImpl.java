package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.InspectionCreateDto;
import by.bsuir.dormitoryinspection.dto.response.InspectionDto;
import by.bsuir.dormitoryinspection.entity.Block;
import by.bsuir.dormitoryinspection.entity.Inspection;
import by.bsuir.dormitoryinspection.entity.InspectorFloor;
import by.bsuir.dormitoryinspection.entity.User;
import by.bsuir.dormitoryinspection.mapper.InspectionMapper;
import by.bsuir.dormitoryinspection.repository.BlockRepository;
import by.bsuir.dormitoryinspection.repository.InspectionRepository;
import by.bsuir.dormitoryinspection.repository.InspectorFloorRepository;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.service.InspectionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

  private final InspectionRepository inspectionRepository;
  private final InspectorFloorRepository inspectorFloorRepository;
  private final UserRepository userRepository;
  private final BlockRepository blockRepository;
  private final InspectionMapper inspectionMapper;

  @Override
  public InspectionDto create(InspectionCreateDto dto, String inspectorUsername) {
    User inspector = userRepository.findByUsername(inspectorUsername)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    Block block = blockRepository.findById(dto.getBlockId())
            .orElseThrow(() -> new EntityNotFoundException("Block not found: " + dto.getBlockId()));
    Set<Integer> assignedFloors = inspectorFloorRepository
            .findAllByInspectorId(inspector.getId())
            .stream()
            .map(InspectorFloor::getFloorNumber)
            .collect(Collectors.toSet());
    if (!assignedFloors.contains(block.getFloor())) {
      throw new AccessDeniedException("Inspector is not assigned to floor " + block.getFloor());
    }

    Inspection inspection = new Inspection();
    inspection.setInspector(inspector);
    inspection.setBlock(block);
    inspection.setDate(dto.getDate());
    inspection.setCreatedAt(LocalDateTime.now());
    inspection.setShower(dto.getShower());
    inspection.setToilet(dto.getToilet());
    inspection.setHall(dto.getHall());
    inspection.setKitchen(dto.getKitchen());
    inspection.setRoomA(dto.getRoomA());
    inspection.setRoomB(dto.getRoomB());

    return inspectionMapper.toDto(inspectionRepository.save(inspection));
  }

  @Override
  public List<InspectionDto> getByBlock(Long blockId) {
    if (!blockRepository.existsById(blockId)) {
      throw new EntityNotFoundException("Block not found: " + blockId);
    }
    return inspectionRepository.findAllByBlockId(blockId)
            .stream()
            .map(inspectionMapper::toDto)
            .toList();
  }

  @Override
  public List<InspectionDto> getMyInspections(String inspectorUsername) {
    User inspector = userRepository.findByUsername(inspectorUsername)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return inspectionRepository.findAllByInspectorId(inspector.getId())
            .stream()
            .map(inspectionMapper::toDto)
            .toList();
  }

  @Override
  public List<InspectionDto> getAll() {
    return inspectionRepository.findAll()
            .stream()
            .map(inspectionMapper::toDto)
            .toList();
  }
}
