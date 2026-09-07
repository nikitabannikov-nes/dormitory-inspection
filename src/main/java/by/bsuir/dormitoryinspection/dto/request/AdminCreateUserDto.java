package by.bsuir.dormitoryinspection.dto.request;

import by.bsuir.dormitoryinspection.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserDto {

  @NotBlank(message = "Username cannot be empty")
  private String username;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  private String fio;

  private Role role;

  private Long blockId;
}
