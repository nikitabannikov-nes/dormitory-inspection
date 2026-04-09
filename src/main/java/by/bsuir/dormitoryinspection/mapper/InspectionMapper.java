package by.bsuir.dormitoryinspection.mapper;

import by.bsuir.dormitoryinspection.dto.response.InspectionDto;
import by.bsuir.dormitoryinspection.entity.Inspection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InspectionMapper {

  @Mapping(source = "block.id", target = "blockId")
  @Mapping(source = "block.number", target = "blockNumber")
  @Mapping(source = "inspector.id", target = "inspectorId")
  @Mapping(source = "inspector.fio", target = "inspectorFio")
  InspectionDto toDto(Inspection inspection);
}
