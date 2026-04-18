package by.bsuir.dormitoryinspection.repository;

import by.bsuir.dormitoryinspection.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

  @Query("SELECT i FROM Inspection i JOIN FETCH i.block JOIN FETCH i.inspector " +
         "WHERE EXTRACT(MONTH FROM i.date) = :month AND EXTRACT(YEAR FROM i.date) = :year " +
         "ORDER BY i.block.floor, i.date, i.block.number")
  List<Inspection> findAllByMonthAndYear(@Param("month") int month, @Param("year") int year);

  List<Inspection> findAllByBlockId(Long blockId);

  List<Inspection> findAllByInspectorId(Long inspectorId);

  List<Inspection> findAllByBlock_Floor(Integer floor);

  void deleteAllByBlockId(Long blockId);

  boolean existsByInspectorIdAndBlockIdAndDate(
          Long inspectorId, Long blockId, LocalDate date);

  @Modifying
  @Query("UPDATE Inspection i SET i.inspector = null WHERE i.inspector.id = :inspectorId")
  void nullifyInspectorByInspectorId(@Param("inspectorId") Long inspectorId);

}
