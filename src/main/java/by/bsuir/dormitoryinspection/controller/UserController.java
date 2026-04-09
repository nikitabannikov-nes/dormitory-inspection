package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.response.UserDto;
import by.bsuir.dormitoryinspection.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public UserDto getMe(Principal principal) {
    return userService.getByUsername(principal.getName());
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<UserDto> getAll() {
    return userService.getAll();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
  @ResponseStatus(HttpStatus.OK)
  public UserDto getById(@PathVariable Long id) {
    return userService.getById(id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }
}
