package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InspectorFloorId implements Serializable {

  private Long inspectorId;
  @Column(name = "floor_number", nullable = false)
  private Integer floorNumber;
}
