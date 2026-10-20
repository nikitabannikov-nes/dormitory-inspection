package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.InspectionCreateDto;
import by.bsuir.dormitoryinspection.dto.request.InspectionUpdateDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionServiceImpl implements InspectionService {

  private static final Duration EDIT_WINDOW = Duration.ofHours(48);

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

    if (inspectionRepository.existsByInspectorIdAndBlockIdAndDate(
            inspector.getId(), block.getId(), dto.getDate())) {
      throw new IllegalArgumentException(
              "Inspector already has an inspection for block " + block.getNumber() + " on " + dto.getDate());
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
    inspection.setRoomB(Boolean.TRUE.equals(block.getHasRoomB()) ? dto.getRoomB() : null);
    inspection.setComment(dto.getComment());

    return toDto(inspectionRepository.save(inspection), inspector);
  }

  @Override
  public InspectionDto update(Long id, InspectionUpdateDto dto, String username) {
    Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Inspection not found: " + id));
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    if (!isOwner(inspection, user)) {
      throw new AccessDeniedException("Only the author can edit this inspection");
    }
    if (!isWithinEditWindow(inspection, user)) {
      throw new AccessDeniedException("Inspection can be edited only within 48 hours after creation");
    }

    inspection.setShower(dto.getShower());
    inspection.setToilet(dto.getToilet());
    inspection.setHall(dto.getHall());
    inspection.setKitchen(dto.getKitchen());
    inspection.setRoomA(dto.getRoomA());
    inspection.setRoomB(Boolean.TRUE.equals(inspection.getBlock().getHasRoomB()) ? dto.getRoomB() : null);
    inspection.setComment(dto.getComment());

    return toDto(inspectionRepository.save(inspection), user);
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
    Set<Integer> assignedFloors = inspectorFloorRepository
            .findAllByInspectorId(inspector.getId())
            .stream()
            .map(InspectorFloor::getFloorNumber)
            .collect(Collectors.toSet());
    if (assignedFloors.isEmpty()) {
      return List.of();
    }
    return inspectionRepository.findAllByBlock_FloorIn(assignedFloors)
            .stream()
            .map(i -> toDto(i, inspector))
            .toList();
  }

  @Override
  public List<InspectionDto> getAll() {
    return inspectionRepository.findAll()
            .stream()
            .map(inspectionMapper::toDto)
            .toList();
  }

  @Override
  public void deleteById(Long id, String username) {
    Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Inspection not found: " + id));
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    if (!isAdmin(user)) {
      if (!isOwner(inspection, user)) {
        throw new AccessDeniedException("Not allowed to delete this inspection");
      }
      if (!isWithinEditWindow(inspection, user)) {
        throw new AccessDeniedException("Inspection can be deleted only within 48 hours after creation");
      }
    }
    inspectionRepository.deleteById(id);
  }

  private InspectionDto toDto(Inspection inspection, User viewer) {
    InspectionDto dto = inspectionMapper.toDto(inspection);
    dto.setEditable(isOwner(inspection, viewer) && isWithinEditWindow(inspection, viewer));
    return dto;
  }

  private boolean isAdmin(User user) {
    return user.getRole().name().equals("ADMIN");
  }

  private boolean isOwner(Inspection inspection, User user) {
    return inspection.getInspector() != null
            && inspection.getInspector().getId().equals(user.getId());
  }

  private boolean isWithinEditWindow(Inspection inspection, User user) {
    return isAdmin(user)
            || LocalDateTime.now().isBefore(inspection.getCreatedAt().plus(EDIT_WINDOW));
  }
}
