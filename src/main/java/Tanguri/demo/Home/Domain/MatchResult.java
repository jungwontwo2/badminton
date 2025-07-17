package Tanguri.demo.Home.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner1_id")
    private User winner1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner2_id")
    private User winner2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loser1_id")
    private User loser1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loser2_id")
    private User loser2;

    private Integer winnerScore;
    private Integer loserScore;

    private Integer mmrChange;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime matchDate;

    //경기 결과 상태를 저장하는 필드
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MatchStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_id")
    private User registeredBy;

    @Builder
    public MatchResult(User winner1, User winner2, User loser1, User loser2, Integer winnerScore, Integer loserScore, User registeredBy) {
        this.winner1 = winner1;
        this.winner2 = winner2;
        this.loser1 = loser1;
        this.loser2 = loser2;
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.registeredBy = registeredBy;
        this.status = MatchStatus.AWAITING_OPPONENT;  // 처음 생성될 때 기본 상태를 '상대방 확인 대기'로 설정
    }

    //상대방이 경기 결과를 확인
    public void confirmByOpponent(){
        this.status = MatchStatus.PENDING_ADMIN;
    }

    //관리자가 경기 결과 승인
    public void confirmByAdmin(){
        this.status = MatchStatus.CONFIRMED;
    }

    //관리자가 경기 결과 거절
    public void rejectMatch(){
        this.status = MatchStatus.REJECTED;
    }

    //MMR 업데이트
    public void setMmrChange(Integer mmrChange){
        this.mmrChange = mmrChange;
    }
}
