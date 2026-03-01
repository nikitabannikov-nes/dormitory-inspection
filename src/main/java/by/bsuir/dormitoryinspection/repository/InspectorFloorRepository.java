package by.bsuir.dormitoryinspection.repository;

import by.bsuir.dormitoryinspection.entity.InspectorFloor;
import by.bsuir.dormitoryinspection.entity.InspectorFloorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectorFloorRepository extends JpaRepository<InspectorFloor, InspectorFloorId> {

  List<InspectorFloor> findAllByInspectorId(Long inspectorId);
}
