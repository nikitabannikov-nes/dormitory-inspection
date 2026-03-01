package by.bsuir.dormitoryinspection.entity;

import by.bsuir.dormitoryinspection.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "username", unique = true, nullable = false)
  private String username;
  @Column(nullable = false)
  private String fio;
  @Column(nullable = false)
  private String password;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "block_id")
  private Block block;
}
