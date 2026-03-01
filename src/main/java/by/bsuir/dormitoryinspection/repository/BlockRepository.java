package by.bsuir.dormitoryinspection.repository;

import by.bsuir.dormitoryinspection.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

  boolean existsByNumber(Integer number);
}
