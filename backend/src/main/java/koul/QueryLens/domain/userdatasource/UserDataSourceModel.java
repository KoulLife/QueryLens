package koul.QueryLens.domain.userdatasource;

import jakarta.persistence.*;
import koul.QueryLens.support.BaseEntity;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_data_source_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "data_source_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDataSourceModel extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "data_source_id", nullable = false)
    private Long dataSourceId;

    public static UserDataSourceModel create(Long userId, Long dataSourceId, boolean exists) {
        if (userId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "사용자 ID가 존재하지 않습니다.");
        }
        if (dataSourceId == null) {
            throw new CoreException(ErrorType.INVALID_INPUT, "데이터소스 ID가 존재하지 않습니다.");
        }
        if (exists) {
            throw new CoreException(ErrorType.DUPLICATE_USER_DATA_SOURCE, "이미 등록된 데이터소스입니다.");
        }
        UserDataSourceModel model = new UserDataSourceModel();
        model.userId = userId;
        model.dataSourceId = dataSourceId;
        return model;
    }
}
