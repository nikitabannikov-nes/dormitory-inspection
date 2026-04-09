package by.bsuir.dormitoryinspection.dto.response;

import by.bsuir.dormitoryinspection.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private Long id;
  private String username;
  private String fio;
  private Role role;
  private Long blockId;
  private Integer blockNumber;
}
