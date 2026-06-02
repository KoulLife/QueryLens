package koul.QueryLens.domain.userdatasource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDataSourceRepository extends JpaRepository<UserDataSourceModel, Long> {

    List<UserDataSourceModel> findAllByUserId(Long userId);

    Optional<UserDataSourceModel> findByUserIdAndDataSourceId(Long userId, Long dataSourceId);

    boolean existsByUserIdAndDataSourceId(Long userId, Long dataSourceId);
}
