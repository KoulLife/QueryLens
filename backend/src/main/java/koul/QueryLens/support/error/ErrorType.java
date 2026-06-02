package koul.QueryLens.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    /** 범용 에러 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "일시적인 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "존재하지 않는 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), "이미 존재하는 리소스입니다."),

    /** User 에러 */
    INVALID_EXTERNAL_IDENTITY(HttpStatus.BAD_REQUEST, "USER_001", "유효하지 않은 외부 사용자 식별자입니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 사용자입니다."),

    /** UserDataSource 에러 */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "UDS_001", "잘못된 입력값입니다."),
    DUPLICATE_USER_DATA_SOURCE(HttpStatus.CONFLICT, "UDS_002", "이미 등록된 데이터소스입니다."),
    USER_DATA_SOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "UDS_003", "등록되지 않은 데이터소스입니다."),

    /** DataSource 에러 */
    DATA_SOURCE_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "DS_001", "데이터소스 연결에 실패했습니다."),

    /** Conversation 에러 */
    SQL_EXECUTION_FAILED(HttpStatus.BAD_REQUEST, "CONV_001", "SQL 실행에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}