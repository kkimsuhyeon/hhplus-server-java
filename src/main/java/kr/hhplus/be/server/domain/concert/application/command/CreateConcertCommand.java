package kr.hhplus.be.server.domain.concert.application.command;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateConcertCommand {

    private String title;
    private String description;

}
