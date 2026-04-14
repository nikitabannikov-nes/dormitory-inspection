package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.SignInDto;
import by.bsuir.dormitoryinspection.dto.request.SignUpDto;
import by.bsuir.dormitoryinspection.dto.response.JwtDto;
import by.bsuir.dormitoryinspection.entity.User;
import by.bsuir.dormitoryinspection.mapper.UserMapper;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.security.JwtService;
import by.bsuir.dormitoryinspection.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public JwtDto signUp(SignUpDto dto) {
    if (!dto.getPassword().trim().equals(dto.getPasswordConfirm().trim())) {
      throw new IllegalArgumentException("Passwords do not match");
    }

    if (userRepository.existsByUsername(dto.getUsername().trim())) {
      throw new IllegalArgumentException("Username already taken");
    }

    User user = userMapper.toEntity(dto);
    user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));

    User saved = userRepository.save(user);

    return jwtService.generateToken(saved);
  }

  @Override
  public JwtDto signIn(SignInDto dto) {
    User user = userRepository.findByUsername(dto.getUsername().trim())
            .orElseThrow(() -> new IllegalArgumentException("Username not found"));

    if (!passwordEncoder.matches(dto.getPassword().trim(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }

    return jwtService.generateToken(user);
  }
}
