package koul.QueryLens.application.user;

import koul.QueryLens.domain.user.ExternalUserIdentity;
import koul.QueryLens.domain.user.UserModel;
import koul.QueryLens.domain.user.UserRepository;
import koul.QueryLens.domain.user.UserSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService {

    private final UserRepository userRepository;

    public UserResult register(String externalUserId, UserSource source) {
        ExternalUserIdentity externalIdentity = new ExternalUserIdentity(externalUserId, source);

        UserModel.validateDuplicate(userRepository.findByExternalIdentity(externalIdentity).isPresent());
        UserModel user = userRepository.save(UserModel.create(externalIdentity));
        return UserResult.from(user);
    }
}
