package by.bsuir.dormitoryinspection.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionDto {

  private Long id;
  private Long blockId;
  private Integer blockNumber;
  private Long inspectorId;
  private String inspectorFio;
  private LocalDate date;
  private LocalDateTime createdAt;
  private Byte shower;
  private Byte toilet;
  private Byte hall;
  private Byte kitchen;
  private Byte roomA;
  private Byte roomB;
  private String comment;
}
