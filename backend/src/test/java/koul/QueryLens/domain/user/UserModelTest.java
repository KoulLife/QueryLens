package koul.QueryLens.domain.user;

import koul.QueryLens.support.error.CoreException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserModelTest {

    @Test
    void create_sets_role_to_USER() {
        ExternalUserIdentity identity = new ExternalUserIdentity("user-001", UserSource.GRAFANA);

        UserModel user = UserModel.create(identity);

        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getExternalIdentity()).isEqualTo(identity);
    }

    @Test
    void create_throws_when_externalIdentity_is_null() {
        assertThatThrownBy(() -> UserModel.create(null))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("외부 사용자 식별자가 존재하지 않습니다.");
    }

    @Test
    void create_throws_when_externalUserId_is_blank() {
        ExternalUserIdentity identity = new ExternalUserIdentity("  ", UserSource.GRAFANA);

        assertThatThrownBy(() -> UserModel.create(identity))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("외부 사용자 ID가 존재하지 않습니다.");
    }

    @Test
    void create_throws_when_source_is_null() {
        ExternalUserIdentity identity = new ExternalUserIdentity("user-001", null);

        assertThatThrownBy(() -> UserModel.create(identity))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("사용자 유입 경로가 존재하지 않습니다.");
    }

    @Test
    void validateDuplicate_throws_when_duplicate() {
        assertThatThrownBy(() -> UserModel.validateDuplicate(true))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("이미 가입된 사용자입니다.");
    }

    @Test
    void validateDuplicate_passes_when_not_duplicate() {
        assertThatCode(() -> UserModel.validateDuplicate(false))
                .doesNotThrowAnyException();
    }
}
