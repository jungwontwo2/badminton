package Tanguri.demo.Home.Config;

import Tanguri.demo.Home.Dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // IllegalStateException 예외 발생하면 이 메소드가 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex){
        //예외 메세지를 담은 ErrorResponse 객체 생성
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        // 400 BadRequest 상태 코드와 함께 ErrorResponse를 응답 본문에 담아 반환
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //EntityNotFoundException 예외 발생하면 이 메소드가 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex){
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        //404 Not Found 상태 코드와 함께 응답 반환
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
