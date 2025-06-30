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
public class MmrChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;

    private Integer changeAmount;
    private Integer resultMmr;

    @CreationTimestamp
    private LocalDateTime logDate;

    @Builder
    public MmrChangeLog(User user, MatchResult matchResult, Integer changeAmount, Integer resultMmr) {
        this.user = user;
        this.matchResult = matchResult;
        this.changeAmount = changeAmount;
        this.resultMmr = resultMmr;
    }
}
