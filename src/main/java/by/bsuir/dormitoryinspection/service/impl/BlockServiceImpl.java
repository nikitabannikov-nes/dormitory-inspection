package by.bsuir.dormitoryinspection.service.impl;

import by.bsuir.dormitoryinspection.dto.request.BlockCreateDto;
import by.bsuir.dormitoryinspection.dto.response.BlockDto;
import by.bsuir.dormitoryinspection.entity.Block;
import by.bsuir.dormitoryinspection.mapper.BlockMapper;
import by.bsuir.dormitoryinspection.repository.BlockRepository;
import by.bsuir.dormitoryinspection.repository.InspectionRepository;
import by.bsuir.dormitoryinspection.repository.UserRepository;
import by.bsuir.dormitoryinspection.service.BlockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockServiceImpl implements BlockService {

  private final BlockRepository blockRepository;
  private final InspectionRepository inspectionRepository;
  private final UserRepository userRepository;
  private final BlockMapper blockMapper;

  @Override
  public List<BlockDto> getAll() {
    return blockRepository.findAll()
            .stream()
            .map(blockMapper::toDto)
            .toList();
  }

  @Override
  public BlockDto getById(Long id) {
    Block block = blockRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Block not found: " + id));
    return blockMapper.toDto(block);
  }

  @Override
  public BlockDto create(BlockCreateDto dto) {
    if (blockRepository.existsByNumber(dto.getNumber())) {
      throw new IllegalArgumentException("Block with number " + dto.getNumber() + " already exists");
    }
    Block block = blockMapper.toEntity(dto);
    return blockMapper.toDto(blockRepository.save(block));
  }

  @Override
  public void deleteById(Long id) {
    if (!blockRepository.existsById(id)) {
      throw new EntityNotFoundException("Block not found: " + id);
    }
    inspectionRepository.deleteAllByBlockId(id);
    userRepository.nullifyBlockByBlockId(id);
    blockRepository.deleteById(id);
  }
}
