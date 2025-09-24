package kr.hhplus.be.server.domain.concert.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateConcertRequest {

    @Schema(description = "콘서트 제목")
    @NotBlank
    private String title;

    @Schema(description = "콘서트 설명")
    private String description;
}
