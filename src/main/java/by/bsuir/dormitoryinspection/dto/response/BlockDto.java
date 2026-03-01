package by.bsuir.dormitoryinspection.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockDto {

  private Long id;
  private Integer number;
  private Integer floor;
}
