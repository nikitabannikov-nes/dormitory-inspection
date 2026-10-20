package by.bsuir.dormitoryinspection.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionUpdateDto {

  @Min(1)
  @Max(5)
  private Byte shower;

  @Min(1)
  @Max(5)
  private Byte toilet;

  @Min(1)
  @Max(5)
  private Byte hall;

  @Min(1)
  @Max(5)
  private Byte kitchen;

  @Min(1)
  @Max(5)
  private Byte roomA;

  @Min(1)
  @Max(5)
  private Byte roomB;

  private String comment;
}
