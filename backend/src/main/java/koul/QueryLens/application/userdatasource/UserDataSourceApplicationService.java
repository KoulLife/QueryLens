package koul.QueryLens.application.userdatasource;

import koul.QueryLens.domain.userdatasource.UserDataSourceModel;
import koul.QueryLens.domain.userdatasource.UserDataSourceRepository;
import koul.QueryLens.support.error.CoreException;
import koul.QueryLens.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDataSourceApplicationService {

    private final UserDataSourceRepository userDataSourceRepository;

    public UserDataSourceResult grant(Long userId, Long dataSourceId) {
        boolean exists = userDataSourceRepository.existsByUserIdAndDataSourceId(userId, dataSourceId);
        UserDataSourceModel model = userDataSourceRepository.save(
                UserDataSourceModel.create(userId, dataSourceId, exists)
        );
        return UserDataSourceResult.from(model);
    }

    @Transactional(readOnly = true)
    public List<UserDataSourceResult> findAllByUserId(Long userId) {
        return userDataSourceRepository.findAllByUserId(userId).stream()
                .map(UserDataSourceResult::from)
                .toList();
    }

    public void revoke(Long userId, Long dataSourceId) {
        UserDataSourceModel model = userDataSourceRepository
                .findByUserIdAndDataSourceId(userId, dataSourceId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_DATA_SOURCE_NOT_FOUND, "등록되지 않은 데이터소스입니다."));
        userDataSourceRepository.delete(model);
    }
}
