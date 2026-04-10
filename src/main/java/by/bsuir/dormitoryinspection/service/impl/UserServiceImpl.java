package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.UserUpdateDto;
import by.bsuir.dormitoryinspection.dto.response.UserDto;
import by.bsuir.dormitoryinspection.entity.Block;
import by.bsuir.dormitoryinspection.entity.User;
import by.bsuir.dormitoryinspection.mapper.UserMapper;
import by.bsuir.dormitoryinspection.repository.BlockRepository;
import by.bsuir.dormitoryinspection.repository.InspectionRepository;
import by.bsuir.dormitoryinspection.repository.InspectorFloorRepository;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BlockRepository blockRepository;
  private final InspectionRepository inspectionRepository;
  private final InspectorFloorRepository inspectorFloorRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto getById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getByUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAll() {
    return userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();
  }

  @Override
  public UserDto update(Long id, UserUpdateDto dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    user.setFio(dto.getFio());
    user.setRole(dto.getRole());

    if (dto.getBlockId() != null) {
      Block block = blockRepository.findById(dto.getBlockId())
              .orElseThrow(() -> new EntityNotFoundException("Block not found: " + dto.getBlockId()));
      user.setBlock(block);
    } else {
      user.setBlock(null);
    }

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found: " + id);
    }
    inspectionRepository.nullifyInspectorByInspectorId(id);
    inspectorFloorRepository.deleteAllByInspectorId(id);
    userRepository.deleteById(id);
  }
}
