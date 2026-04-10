package by.bsuir.dormitoryinspection.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockCreateDto {

  @NotNull
  @Min(1)
  private Integer number;

  @NotNull
  @Min(2)
  @Max(16)
  private Integer floor;

  @NotNull
  private Boolean hasRoomB;
}
