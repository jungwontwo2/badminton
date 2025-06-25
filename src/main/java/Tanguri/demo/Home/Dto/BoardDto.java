package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BoardDto {
    private String nttId;
    private String bbsId;
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdDate;

    @Builder
    public BoardDto(String nttId, String bbsId, String title, String author, String content, LocalDateTime createdDate) {
        this.nttId = nttId;
        this.bbsId = bbsId;
        this.title = title;
        this.author = author;
        this.content = content;
        this.createdDate = createdDate;
    }

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static BoardDto fromEntity(Board board) {
        return BoardDto.builder()
                .nttId(board.getNttId())
                .bbsId(board.getBbsId())
                .title(board.getNttSj())
                .author(board.getNtcrNm())
                .content(board.getNttCn())
                .createdDate(board.getFrstRegistPnttm())
                .build();
    }
}
