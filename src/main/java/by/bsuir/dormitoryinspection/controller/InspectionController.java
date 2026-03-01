package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.request.InspectionCreateDto;
import by.bsuir.dormitoryinspection.dto.response.InspectionDto;
import by.bsuir.dormitoryinspection.service.InspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

  private final InspectionService inspectionService;

  @PostMapping
  @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public InspectionDto create(@Valid @RequestBody InspectionCreateDto dto, Principal principal) {
    return inspectionService.create(dto, principal.getName());
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<InspectionDto> getAll() {
    return inspectionService.getAll();
  }

  @GetMapping("/my")
  @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<InspectionDto> getMy(Principal principal) {
    return inspectionService.getMyInspections(principal.getName());
  }

  @GetMapping("/block/{blockId}")
  @PreAuthorize("hasAnyRole('USER', 'INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<InspectionDto> getByBlock(@PathVariable Long blockId) {
    return inspectionService.getByBlock(blockId);
  }
}
