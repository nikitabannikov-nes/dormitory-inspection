package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class InspectorFloorId implements Serializable {

  private Long inspector;
  private Integer floorNumber;
}
