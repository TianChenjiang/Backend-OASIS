package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.DataSourceBlService;
import com.rubiks.backendoasis.exception.FileFormatNotSupportException;
import com.rubiks.backendoasis.model.ImportPaperRes;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.response.Response;
import com.rubiks.backendoasis.response.SuccessResponse;
import com.rubiks.backendoasis.response.WrongResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DataSourceController {
    private DataSourceBlService dataSourceBlService;

    @Autowired
    public DataSourceController(DataSourceBlService dataSourceBlService) {
        this.dataSourceBlService = dataSourceBlService;
    }

    @PostMapping("/import/paper")
    @ApiOperation(value = "接口 6导入论文数据")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<Response> importPaperData(@RequestParam("paperData") MultipartFile paperData) {
        try {
            return dataSourceBlService.importPaperData(paperData);
        } catch (FileFormatNotSupportException e) {
            return new BasicResponse<>(e.getCode(), e.getMessage(), null);
        }
    }
}
