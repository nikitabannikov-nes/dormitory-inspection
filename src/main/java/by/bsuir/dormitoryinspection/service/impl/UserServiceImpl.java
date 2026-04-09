package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.response.UserDto;
import by.bsuir.dormitoryinspection.entity.User;
import by.bsuir.dormitoryinspection.mapper.UserMapper;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto getById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    return userMapper.toDto(user);
  }

  @Override
  public UserDto getByUsername(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    return userMapper.toDto(user);
  }

  @Override
  public List<UserDto> getAll() {
    return userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();
  }

  @Override
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User not found: " + id);
    }
    userRepository.deleteById(id);
  }
}
