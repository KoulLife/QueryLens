package koul.QueryLens.api.datasource;

import jakarta.validation.Valid;
import koul.QueryLens.api.ApiResponse;
import koul.QueryLens.application.datasource.DataSourceApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data-sources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceApplicationService dataSourceApplicationService;

    @PostMapping
    public ApiResponse<DataSourceResponse> create(@RequestBody @Valid CreateDataSourceRequest request) {
        return ApiResponse.success(
                DataSourceResponse.from(dataSourceApplicationService.create(request.dbType(), request.connection()))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<DataSourceResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(
                DataSourceResponse.from(dataSourceApplicationService.findById(id))
        );
    }

    @PostMapping("/{id}/schema-sync")
    public ApiResponse<DataSourceResponse> syncSchema(@PathVariable Long id) {
        return ApiResponse.success(
                DataSourceResponse.from(dataSourceApplicationService.syncSchema(id))
        );
    }
}
