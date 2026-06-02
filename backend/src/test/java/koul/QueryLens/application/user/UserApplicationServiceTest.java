package koul.QueryLens.application.user;

import koul.QueryLens.domain.user.UserModel;
import koul.QueryLens.domain.user.UserRepository;
import koul.QueryLens.domain.user.UserSource;
import koul.QueryLens.support.error.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @InjectMocks
    private UserApplicationService userApplicationService;

    @Mock
    private UserRepository userRepository;

    @Test
    void register_returns_UserResult_on_success() {
        given(userRepository.findByExternalIdentity(any())).willReturn(Optional.empty());
        given(userRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        UserResult result = userApplicationService.register("user-001", UserSource.GRAFANA);

        assertThat(result.externalUserId()).isEqualTo("user-001");
        assertThat(result.source()).isEqualTo(UserSource.GRAFANA);
    }

    @Test
    void register_throws_when_duplicate_user() {
        given(userRepository.findByExternalIdentity(any())).willReturn(
                Optional.of(mock(UserModel.class))
        );

        assertThatThrownBy(() -> userApplicationService.register("user-001", UserSource.GRAFANA))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("이미 가입된 사용자입니다.");
    }
}
