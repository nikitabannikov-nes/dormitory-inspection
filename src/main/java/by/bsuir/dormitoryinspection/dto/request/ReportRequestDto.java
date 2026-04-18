package by.bsuir.dormitoryinspection.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportRequestDto {

  @NotNull
  @Min(1)
  @Max(12)
  private Integer month;

  @NotBlank
  private String chairmanFio;

  private List<String> performedWork;
}
