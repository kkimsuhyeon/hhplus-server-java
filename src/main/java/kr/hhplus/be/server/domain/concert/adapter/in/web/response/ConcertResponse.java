package kr.hhplus.be.server.domain.concert.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ConcertResponse", description = "콘서트 정보")
public class ConcertResponse {

    @Schema(description = "콘서트 고유값")
    private String id;

    @Schema(description = "콘서트 제목")
    private String title;

    @Schema(description = "콘서트 설명")
    private String description;

    public static ConcertResponse fromEntity(ConcertEntity entity) {
        return ConcertResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .build();
    }

}
