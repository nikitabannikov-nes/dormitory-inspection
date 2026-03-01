package by.bsuir.dormitoryinspection.service;

import by.bsuir.dormitoryinspection.dto.request.BlockCreateDto;
import by.bsuir.dormitoryinspection.dto.response.BlockDto;

import java.util.List;

public interface BlockService {

  List<BlockDto> getAll();

  BlockDto getById(Long id);

  BlockDto create(BlockCreateDto dto);

  void deleteById(Long id);
}
