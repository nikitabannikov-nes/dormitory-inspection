package by.bsuir.dormitoryinspection.security;

import by.bsuir.dormitoryinspection.dto.response.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthentificationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;


  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ErrorDto errorDto = new ErrorDto(401, authException.getMessage());
    objectMapper.writeValue(response.getOutputStream(), errorDto);
  }
}
