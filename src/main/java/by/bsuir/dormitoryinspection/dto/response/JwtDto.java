package by.bsuir.dormitoryinspection.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtDto {

  private String accessToken;

  public JwtDto(String accessToken) {
    this.accessToken = accessToken;
  }
}
