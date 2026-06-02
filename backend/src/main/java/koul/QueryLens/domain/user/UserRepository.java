package koul.QueryLens.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByExternalIdentity(ExternalUserIdentity externalIdentity);
}
