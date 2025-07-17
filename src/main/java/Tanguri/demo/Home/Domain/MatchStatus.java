package Tanguri.demo.Home.Domain;

public enum MatchStatus {
    AWAITING_OPPONENT, //상대방 확인 대기
    PENDING_ADMIN,    //관리자 승인 대기
    CONFIRMED,  //확정
    REJECTED    //거절
}
