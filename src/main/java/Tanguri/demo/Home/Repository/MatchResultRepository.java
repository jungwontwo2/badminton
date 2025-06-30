package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchResultRepository extends JpaRepository<MatchResult,Long> {
}
