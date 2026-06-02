package koul.QueryLens.domain.user;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"external_user_id", "source"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModel extends BaseEntity {

    @Embedded
    private ExternalUserIdentity externalIdentity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public static void validateDuplicate(boolean exists) {
        if (exists) {
            throw new CoreException(ErrorType.DUPLICATE_USER, "이미 가입된 사용자입니다.");
        }
    }

    public static UserModel create(ExternalUserIdentity externalIdentity) {
        if (externalIdentity == null) {
            throw new CoreException(ErrorType.INVALID_EXTERNAL_IDENTITY, "외부 사용자 식별자가 존재하지 않습니다.");
        }
        if (externalIdentity.source() == null) {
            throw new CoreException(ErrorType.INVALID_EXTERNAL_IDENTITY, "사용자 유입 경로가 존재하지 않습니다.");
        }
        if (externalIdentity.externalUserId() == null || externalIdentity.externalUserId().isBlank()) {
            throw new CoreException(ErrorType.INVALID_EXTERNAL_IDENTITY, "외부 사용자 ID가 존재하지 않습니다.");
        }

        UserModel user = new UserModel();
        user.externalIdentity = externalIdentity;
        user.role = UserRole.USER;
        return user;
    }
}
