package Tanguri.demo.Home.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comtnbbs")
public class Board {

    @Id
    @Column(name = "NTT_ID", nullable = false) // 'NTT_ID'를 기본 키(Primary Key)로 지정합니다.
    private String nttId; // JpaRepository<Board, String>의 'String'과 타입이 일치해야 합니다.

    @Column(name = "BBS_ID")
    private String bbsId;//게시판 Id

    @Column(name = "NTT_SJ") // 게시글 제목
    private String nttSj;

    @Column(name = "NTT_CN") // 게시글 내용
    private String nttCn;

    @Column(name = "NTCR_NM") // 작성자명
    private String ntcrNm;

    @Column(name = "FRST_REGIST_PNTTM") // 최초 등록 시점 (정렬의 기준이 되는 컬럼)
    private LocalDateTime frstRegistPnttm;
}
