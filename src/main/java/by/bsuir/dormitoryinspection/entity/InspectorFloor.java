package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspector_floors")
@IdClass(InspectorFloorId.class)
public class InspectorFloor {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inspector_id", nullable = false)
  private User inspector;

  @Id
  @Column(name = "floor_number", nullable = false)
  private Integer floorNumber;
}
