package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.request.SignInDto;
import by.bsuir.dormitoryinspection.dto.request.SignUpDto;
import by.bsuir.dormitoryinspection.dto.response.JwtDto;
import by.bsuir.dormitoryinspection.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signUp")
  @ResponseStatus(HttpStatus.CREATED)
  public JwtDto signUp(@Valid @RequestBody SignUpDto signUpDto) {
    return authService.signUp(signUpDto);
  }

  @PostMapping("/signIn")
  @ResponseStatus(HttpStatus.OK)
  public JwtDto signIn(@Valid @RequestBody SignInDto signInDto) {
    return authService.signIn(signInDto);
  }
}
