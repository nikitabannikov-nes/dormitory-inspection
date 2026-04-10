package by.bsuir.dormitoryinspection.repository;

import by.bsuir.dormitoryinspection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  @org.springframework.data.jpa.repository.Modifying
  @org.springframework.data.jpa.repository.Query("UPDATE User u SET u.block = null WHERE u.block.id = :blockId")
  void nullifyBlockByBlockId(@org.springframework.data.repository.query.Param("blockId") Long blockId);
}
