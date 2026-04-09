package by.bsuir.dormitoryinspection.dto.request;

import by.bsuir.dormitoryinspection.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

  @NotBlank
  private String fio;

  @NotNull
  private Role role;

  private Long blockId;
}
