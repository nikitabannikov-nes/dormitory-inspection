package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspector_floors")
public class InspectorFloor {

  @EmbeddedId
  private InspectorFloorId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("inspectorId")
  @JoinColumn(name = "inspector_id", nullable = false)
  private User inspector;

  public Integer getFloorNumber() {
    return id != null ? id.getFloorNumber() : null;
  }
}
