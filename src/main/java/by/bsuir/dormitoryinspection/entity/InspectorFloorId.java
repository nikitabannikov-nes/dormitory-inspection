package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class InspectorFloorId implements Serializable {

  private Long inspectorId;
  private Integer floorNumber;
}
