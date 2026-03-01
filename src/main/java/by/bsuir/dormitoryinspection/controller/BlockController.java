package by.bsuir.dormitoryinspection.controller;

import by.bsuir.dormitoryinspection.dto.request.BlockCreateDto;
import by.bsuir.dormitoryinspection.dto.response.BlockDto;
import by.bsuir.dormitoryinspection.service.BlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

  private final BlockService blockService;

  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public List<BlockDto> getAll() {
    return blockService.getAll();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER', 'INSPECTOR', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public BlockDto getById(@PathVariable Long id) {
    return blockService.getById(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public BlockDto create(@Valid @RequestBody BlockCreateDto dto) {
    return blockService.create(dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    blockService.deleteById(id);
  }

}
