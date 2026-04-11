package by.bsuir.dormitoryinspection.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionCreateDto {

  @NotNull
  private Long blockId;

  @NotNull
  private LocalDate date;

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
