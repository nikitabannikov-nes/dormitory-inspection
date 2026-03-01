package by.bsuir.dormitoryinspection.repository;

import by.bsuir.dormitoryinspection.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

  List<Inspection> findAllByBlockId(Long blockId);

  List<Inspection> findAllByInspectorId(Long inspectorId);

  List<Inspection> findAllByBlock_Floor(Integer floor);
}
