package by.bsuir.dormitoryinspection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inspections")
public class Inspection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inspector_id")
  private User inspector;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "block_id")
  private Block block;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private Byte shower;

  @Column(nullable = false)
  private Byte toilet;

  @Column(nullable = false)
  private Byte hall;

  @Column(nullable = false)
  private Byte kitchen;

  @Column(name = "room_a", nullable = false)
  private Byte roomA;

  @Column(name = "room_b", nullable = false)
  private Byte roomB;
}
