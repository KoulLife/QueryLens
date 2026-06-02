package koul.QueryLens.api.user;

import jakarta.validation.Valid;
import koul.QueryLens.api.ApiResponse;
import koul.QueryLens.application.user.UserApplicationService;
import koul.QueryLens.application.user.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    public ApiResponse<UserResponse> register(@RequestBody @Valid CreateUserRequest request) {
        UserResult result = userApplicationService.register(request.externalUserId(), request.source());
        return ApiResponse.success(UserResponse.from(result));
    }
}
