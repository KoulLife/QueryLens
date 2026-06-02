package koul.QueryLens.domain.user;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
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
}
