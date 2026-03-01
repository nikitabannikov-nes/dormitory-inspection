package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.SignInDto;
import by.bsuir.dormitoryinspection.dto.request.SignUpDto;
import by.bsuir.dormitoryinspection.dto.response.JwtDto;

public interface AuthService {

  JwtDto signUp(SignUpDto signUpDto);

  JwtDto signIn(SignInDto signInDto);
}
