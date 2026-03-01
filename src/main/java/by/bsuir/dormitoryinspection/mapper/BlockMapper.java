package by.bsuir.dormitoryinspection.mapper;

import by.bsuir.dormitoryinspection.dto.request.BlockCreateDto;
import by.bsuir.dormitoryinspection.dto.response.BlockDto;
import by.bsuir.dormitoryinspection.entity.Block;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BlockMapper {

  BlockDto toDto(Block block);

  Block toEntity(BlockCreateDto blockDto);
}
