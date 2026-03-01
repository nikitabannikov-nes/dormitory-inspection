package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.request.AssignFloorDto;
import by.bsuir.dormitoryinspection.service.InspectorFloorService;
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

import java.util.List;

@RestController
@RequestMapping("/api/users/{id}/floors")
@RequiredArgsConstructor
public class InspectorFloorController {

  private final InspectorFloorService inspectorFloorService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void assignFloors(@PathVariable Long id,
                           @Valid @RequestBody AssignFloorDto dto) {
    inspectorFloorService.assignFloors(id, dto);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<Integer> getFloors(@PathVariable Long id) {
    return inspectorFloorService.getFloors(id);
  }
}
