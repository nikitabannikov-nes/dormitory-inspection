package by.bsuir.dormitoryinspection.dto.request;

import by.bsuir.dormitoryinspection.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

  @NotBlank(message = "Username cannot be empty")
  private String username;

  @NotBlank(message = "FIO cannot be empty")
  private String fio;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  private String passwordConfirm;

  private Role role;
}
