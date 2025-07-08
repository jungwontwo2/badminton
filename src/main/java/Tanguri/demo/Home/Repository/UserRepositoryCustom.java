package Tanguri.demo.Home.Repository;

import Tanguri.demo.Home.Domain.User;
import Tanguri.demo.Home.Dto.RankingDto;
import Tanguri.demo.Home.Dto.SearchCond;

import java.util.List;

public interface UserRepositoryCustom {
    //여러 검색 조건을 받아 동적으로 쿼리를 생성하여 랭킹을 조회
    List<User> searchUsers(SearchCond searchCond);
}
