package by.bsuir.dormitoryinspection.exception;

import by.bsuir.dormitoryinspection.dto.response.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleNotFound(EntityNotFoundException e) {
    return new ErrorDto(404, e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleBadRequest(IllegalArgumentException e) {
    return new ErrorDto(400, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleBadRequest(MethodArgumentNotValidException e) {
    String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    return new ErrorDto(400, message);
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorDto handleAccessDenied(AccessDeniedException e) {
    return new ErrorDto(403, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDto handleInternalServerError(Exception e) {
    return new ErrorDto(500, "Internal server error");
  }

//  @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
//  @ResponseStatus(HttpStatus.UNAUTHORIZED)
//  public ErrorDto handleUnauthorized(HttpClientErrorException.Unauthorized e) {
//    return new ErrorDto(401, e.getMessage());
//  }
}
