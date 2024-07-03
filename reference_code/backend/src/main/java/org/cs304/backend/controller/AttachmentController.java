package org.cs304.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Resource
    private IAttachmentService attachmentService;

    @PostMapping("/getById")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"id\": 1}")))
    @Operation(summary = "根据ID获取附件信息",
            description = """
                    ### 参数 ###
                    id(Integer): 附件ID
                    ### 返回值 ###
                    {
                      "id": 1,
                      "fileDir": "test",
                      "fileName": "test.txt",
                      "fileSize": 1024,
                      "fileType": "txt",
                      "fileUrl": "http://localhost:8080/attachment/download/1",
                      "createTime": "2021-08-01 00:00:00",
                      "updateTime": "2021-08-01 00:00:00"
                    }
                    ### 实现逻辑 ###
                    1. 根据ID查询附件信息
                    2. 返回附件信息
                    """)
    public Result getById(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        Integer id = requestBody.getInteger("id");
        return Result.success(response, attachmentService.getById(userType, id));
    }

    @PostMapping("/getBatchByIds")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"idList\": [1, 2, 3]}")))
    @Operation(summary = "根据ID列表批量获取附件信息",
            description = """
                    ### 参数 ###
                    idList(List<Integer>): 附件ID列表
                    ### 返回值 ###
                    [
                      {
                        "id": 1,
                        "fileDir": "test",
                        "fileName": "test.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/1",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "id": 2,
                        "fileDir": "test1",
                        "fileName": "test1.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/2",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "id": 3,
                        "fileDir": "test2",
                        "fileName": "test2.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/3",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      }
                    ]
                    ### 实现逻辑 ###
                    1. 根据ID列表查询附件信息
                    2. 返回附件信息列表
                    """)
    public Result getBatchByIds(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        List<Integer> idList = requestBody.getList("idList", Integer.class);
        return Result.success(response, attachmentService.getBatchByIds(userType, idList));
    }

    @PostMapping("/uploadStart")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"fileDir\": \"test\"}")))
    @Operation(summary = "上传文件开始",
            description = """
                    ### 参数 ###
                    fileDir(String): 文件目录
                    ### 返回值 ###
                    {
                      "fileDir": "test",
                      "fileName": "test.txt",
                      "fileSize": 1024,
                      "fileType": "txt",
                      "fileUrl": "http://localhost:8080/attachment/download/1",
                      "createTime": "2021-08-01 00:00:00",
                      "updateTime": "2021-08-01 00:00:00"
                    }
                    ### 实现逻辑 ###
                    1. 上传文件开始
                    2. 返回附件信息
                    """)
    public Result uploadStart(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        String fileDir = requestBody.getString("fileDir");
        return Result.success(response, attachmentService.uploadStart(userType, fileDir, null));
    }

    @PostMapping("/uploadFinish")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"filePath\": \"test.txt\", \"requestData\": {}}")))
    @Operation(summary = "上传文件结束",
            description = """
                    ### 参数 ###
                    filePath(String): 文件路径
                    requestData(Object): 请求数据
                    ### 返回值 ###
                    {
                      "id": 1,
                      "fileDir": "test",
                      "fileName": "test.txt",
                      "fileSize": 1024,
                      "fileType": "txt",
                      "fileUrl": "http://localhost:8080/attachment/download/1",
                      "createTime": "2021-08-01 00:00:00",
                      "updateTime": "2021-08-01 00:00:00"
                    }
                    ### 实现逻辑 ###
                    1. 上传文件结束
                    2. 返回附件信息
                    """)
    public Result uploadFinish(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        String filePath = requestBody.getString("filePath");
        JSONObject requestData = requestBody.getJSONObject("requestData");
        return Result.success(response, attachmentService.uploadFinish(userType, filePath, requestData, false));
    }

    @PostMapping("/uploadBatchStart")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("""
            {
              "fileDirList": ["test", "test1", "test2"],
              "fileNameList": ["file.txt", "file1.txt", "file2.txt"]
            }""")))
    @Operation(summary = "批量上传文件开始",
            description = """
                    ### 参数 ###
                    fileDirList(List<String>): 文件目录列表
                    fileNameList(List<String>): 文件名列表
                    ### 返回值 ###
                    [
                      {
                        "fileDir": "test",
                        "fileName": "test.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/1",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "fileDir": "test1",
                        "fileName": "test1.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/2",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "fileDir": "test2",
                        "fileName": "test2.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/3",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      }
                    ]
                    ### 实现逻辑 ###
                    1. 批量上传文件开始
                    2. 返回附件信息列表
                    """)
    public Result uploadBatchStart(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        List<String> fileDirList = requestBody.getList("fileDirList", String.class);
        List<String> fileNameList = requestBody.getList("fileNameList", String.class);
        return Result.success(response, attachmentService.uploadBatchStart(userType, fileDirList, fileNameList, null));
    }

    @PostMapping("/uploadBatchFinish")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"filePathList\": [\"test/file.txt\", \"test1/file1.txt\", \"test2/file2.txt\"], \"requestData\": {}}")))
    @Operation(summary = "批量上传文件结束",
            description = """
                    ### 参数 ###
                    filePathList(List<String>): 文件路径列表
                    requestData(Object): 请求数据
                    ### 返回值 ###
                    [
                      {
                        "id": 1,
                        "fileDir": "test",
                        "fileName": "test.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/1",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "id": 2,
                        "fileDir": "test1",
                        "fileName": "test1.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/2",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      },
                      {
                        "id": 3,
                        "fileDir": "test2",
                        "fileName": "test2.txt",
                        "fileSize": 1024,
                        "fileType": "txt",
                        "fileUrl": "http://localhost:8080/attachment/download/3",
                        "createTime": "2021-08-01 00:00:00",
                        "updateTime": "2021-08-01 00:00:00"
                      }
                    ]
                    ### 实现逻辑 ###
                    1. 批量上传文件结束
                    2. 返回附件信息列表
                    """)
    public Result uploadBatchFinish(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        List<String> filePathList = requestBody.getList("filePathList", String.class);
        JSONObject requestData = requestBody.getJSONObject("requestData");
        return Result.success(response, attachmentService.uploadBatchFinish(userType, filePathList, requestData));
    }

    @PostMapping("/deleteById")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"id\": 1}")))
    @Operation(summary = "根据ID删除附件",
            description = """
                    ### 参数 ###
                    id(Integer): 附件ID
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 根据ID删除附件
                    2. 返回成功信息
                    """)
    public Result deleteById(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        Integer id = requestBody.getInteger("id");
        attachmentService.deleteById(userType, id);
        return Result.success(response);
    }

    @PostMapping("/deleteBatchByIdList")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(examples = @ExampleObject("{\"idList\": [1, 2, 3]}")))
    @Operation(summary = "根据ID列表批量删除附件",
            description = """
                    ### 参数 ###
                    idList(List<Integer>): 附件ID列表
                    ### 返回值 ###
                    无
                    ### 实现逻辑 ###
                    1. 根据ID列表批量删除附件
                    2. 返回成功信息
                    """)
    public Result deleteBatchByIdList(HttpServletResponse response, HttpServletRequest request, @RequestBody JSONObject requestBody) {
        int userType = (int) request.getAttribute("loginUserType");
        List<Integer> idList = requestBody.getList("idList", Integer.class);
        attachmentService.deleteBatchByIdList(userType, idList);
        return Result.success(response);
    }
}
