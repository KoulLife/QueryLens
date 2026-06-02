package koul.QueryLens.api.userdatasource;

import jakarta.validation.Valid;
import koul.QueryLens.api.ApiResponse;
import koul.QueryLens.application.userdatasource.UserDataSourceApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/data-sources")
@RequiredArgsConstructor
public class UserDataSourceController {

    private final UserDataSourceApplicationService userDataSourceApplicationService;

    @PostMapping
    public ApiResponse<UserDataSourceResponse> grant(
            @PathVariable Long userId,
            @RequestBody @Valid GrantUserDataSourceRequest request
    ) {
        return ApiResponse.success(
                UserDataSourceResponse.from(userDataSourceApplicationService.grant(userId, request.dataSourceId()))
        );
    }

    @GetMapping
    public ApiResponse<List<UserDataSourceResponse>> findAll(@PathVariable Long userId) {
        List<UserDataSourceResponse> responses = userDataSourceApplicationService.findAllByUserId(userId).stream()
                .map(UserDataSourceResponse::from)
                .toList();
        return ApiResponse.success(responses);
    }

    @DeleteMapping("/{dataSourceId}")
    public ApiResponse<?> revoke(
            @PathVariable Long userId,
            @PathVariable Long dataSourceId
    ) {
        userDataSourceApplicationService.revoke(userId, dataSourceId);
        return ApiResponse.success();
    }
}
