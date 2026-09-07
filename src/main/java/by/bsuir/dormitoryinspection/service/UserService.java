package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.AdminCreateUserDto;
import by.bsuir.dormitoryinspection.dto.request.UserUpdateDto;
import by.bsuir.dormitoryinspection.dto.response.UserDto;

import java.util.List;

public interface UserService {

  UserDto getById(Long id);

  UserDto getByUsername(String username);

  List<UserDto> getAll();

  UserDto createByAdmin(AdminCreateUserDto dto);

  UserDto update(Long id, UserUpdateDto dto);

  void deleteById(Long id);
}
