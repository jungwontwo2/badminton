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

    @Builder
    public MatchResult(User winner1, User winner2, User loser1, User loser2, Integer winnerScore, Integer loserScore, Integer mmrChange) {
        this.winner1 = winner1;
        this.winner2 = winner2;
        this.loser1 = loser1;
        this.loser2 = loser2;
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.mmrChange = mmrChange;
    }
}
