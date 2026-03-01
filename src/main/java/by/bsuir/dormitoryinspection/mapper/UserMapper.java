package by.bsuir.dormitoryinspection.mapper;

import by.bsuir.dormitoryinspection.dto.request.SignUpDto;
import by.bsuir.dormitoryinspection.dto.response.UserDto;
import by.bsuir.dormitoryinspection.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  UserDto toDto(User user);

  User toEntity(SignUpDto dto);

}
