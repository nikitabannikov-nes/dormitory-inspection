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

  @NotNull
  @Min(1)
  @Max(5)
  private Byte shower;

  @NotNull
  @Min(1)
  @Max(5)
  private Byte toilet;

  @NotNull
  @Min(1)
  @Max(5)
  private Byte hall;

  @NotNull
  @Min(1)
  @Max(5)
  private Byte kitchen;

  @NotNull
  @Min(1)
  @Max(5)
  private Byte roomA;

  @NotNull
  @Min(1)
  @Max(5)
  private Byte roomB;
}
