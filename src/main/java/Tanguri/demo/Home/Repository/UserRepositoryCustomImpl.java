package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Domain.UserStatus;
import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Dto.SearchCond;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.util.List;

import static Tanguri.demo.Home.Domain.QUser.user;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<User> searchUsers(SearchCond searchCond) {

        return queryFactory
                .selectFrom(user)
                .where(user.status.eq(UserStatus.VERIFIED),
                        nicknameContains(searchCond.getNickname()),
                        clubContains(searchCond.getClub()),
                        ageGroupEq(searchCond.getAgeGroup()),
                        gradeContains(searchCond.getGrade())
                )
                .orderBy(user.mmr.desc())
                .fetch();
    }

    private BooleanExpression nicknameContains(String nickname){
        return StringUtils.hasText(nickname) ? user.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression clubContains(String club){
        return StringUtils.hasText(club) ? user.club.containsIgnoreCase(club) : null;
    }

    private BooleanExpression ageGroupEq(String ageGroup){
        return StringUtils.hasText(ageGroup) ? user.ageGroup.eq(ageGroup) : null;
    }

    private BooleanExpression gradeContains(String grade){
        if(!StringUtils.hasText(grade)){
            return null;
        }
        return user.gradeGu.containsIgnoreCase(grade)
                .or(user.gradeSi.containsIgnoreCase(grade))
                .or(user.gradeNational.containsIgnoreCase(grade));
    }


}
