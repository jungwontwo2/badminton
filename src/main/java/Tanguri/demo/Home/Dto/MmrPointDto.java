package Tanguri.demo.Home.Dto;

import Tanguri.demo.Home.Domain.MmrChangeLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MmrPointDto {

    //그래프 x축에 표시될 날짜(예: 2025-06-06)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime date;

    //그래프 y축에 표시될 MMR 점수
    private final Integer mmr;

    public MmrPointDto(MmrChangeLog log){
        this.date = log.getLogDate();
        this.mmr = log.getResultMmr();
    }
}
