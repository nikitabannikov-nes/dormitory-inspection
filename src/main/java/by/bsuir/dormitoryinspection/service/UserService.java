package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.response.UserDto;

import java.util.List;

public interface UserService {

  UserDto getById(Long id);

  UserDto getByUsername(String username);

  List<UserDto> getAll();

  void deleteById(Long id);
}
