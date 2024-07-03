package org.eventCenter.fileServer.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.eventCenter.fileServer.component.GlobalData;
import org.eventCenter.fileServer.exception.ServiceException;
import org.eventCenter.fileServer.service.IFileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    @Value("${backend.host:}")
    private String backendHost;

    @Value("${backend.port:}")
    private String backendPort;

    @Value("${backend.api-token:}")
    private String backendApiToken;

    @Override
    public ResponseEntity<?> download(String filePath) {
        if (filePath == null) {
            throw new ServiceException("400", "This API can not be used with admin token");
        }
//        System.out.println(GlobalData.FILE_DIRECTORY);
//        System.out.println(filePath);
        File file = new File(GlobalData.FILE_DIRECTORY, filePath);
        if (!file.isFile()) {
            throw new ServiceException("500", "File not exist");
        }
        try {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName().substring(33), StandardCharsets.UTF_8)).body(new InputStreamResource(new FileInputStream(file)));
        } catch (IOException e) {
            throw new ServiceException("500", "Download failed");
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null) {
            throw new ServiceException("400", "Invalid data");
        }
        File file = new File(GlobalData.FILE_DIRECTORY, filePath);
        if (!file.isFile()) {
            throw new ServiceException("400", "File not exist");
        }
        try {
            boolean result = file.delete();
            if (!result) {
                throw new ServiceException("500", "Delete failed");
            }
        } catch (SecurityException e) {
            throw new ServiceException("500", "Delete failed");
        }
    }

    @Override
    public Object upload(String requestInfo, MultipartFile inputFile, boolean mode) {
        if (backendHost.isBlank() || backendPort.isBlank() || backendApiToken.isBlank()) {
            throw new ServiceException("500", "This API is currently unavailable");
        }
        if (requestInfo == null) {
            throw new ServiceException("400", "This API can not be used with admin token");
        }
        JSONObject requestInfoJsonObject = null;
        String fileDir;
        if (mode) {
            requestInfoJsonObject = JSONObject.parseObject(requestInfo);
            fileDir = requestInfoJsonObject.getString("fileDir");
        }
        else {
            fileDir = requestInfo;
        }
        if (fileDir == null) {
            throw new ServiceException("500", "Invalid file path");
        }
        String fileDirWithUUID = fileDir.replace("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
        if (!fileDirWithUUID.equals(fileDir)) {
            File dir = Paths.get(GlobalData.FILE_DIRECTORY, fileDirWithUUID).toFile();
            while (true) {
                if (!dir.exists()) {
                    break;
                }
                else {
                    fileDirWithUUID = fileDir.replace("uuid", UUID.randomUUID().toString().replaceAll("-", ""));
                    dir = Paths.get(GlobalData.FILE_DIRECTORY, fileDirWithUUID).toFile();
                }
            }
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "-" + inputFile.getOriginalFilename();
        File file = Paths.get(GlobalData.FILE_DIRECTORY, fileDirWithUUID, fileName).toFile();
        while (true) {
            if (!file.exists()) {
                break;
            }
            else {
                fileName = UUID.randomUUID().toString().replaceAll("-", "") + "-" + inputFile.getOriginalFilename();
                file = Paths.get(GlobalData.FILE_DIRECTORY, fileDirWithUUID, fileName).toFile();
            }
        }
        String filePathStringForDatabase = Paths.get(fileDirWithUUID, fileName).toString();
        boolean result = file.mkdirs();
        if (!result) {
            throw new ServiceException("500", "Unable to create the directory");
        }
        else {
            try {
                inputFile.transferTo(file);
                if (mode) {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("filePath", filePathStringForDatabase);
                    requestBody.put("requestData", requestInfoJsonObject.getJSONObject("requestData"));
                    httpHeaders.set("token", backendApiToken);
                    try {
                        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
                        ResponseEntity<JSONObject> responseFromBackend = restTemplate.exchange("http://" + backendHost + ":" + backendPort + "/attachment/uploadFinish", HttpMethod.POST, httpEntity, JSONObject.class);
                        if (responseFromBackend.getStatusCode().value() != 200) {
                            throw new ServiceException("500", "An error occurred when communicating with the backend");
                        }
                        else {
                            return Objects.requireNonNull(responseFromBackend.getBody()).getJSONObject("data");
                        }
                    } catch (RestClientException e) {
                        throw new ServiceException("500", "An error occurred when communicating with the backend");
                    }
                }
                else {
                    return filePathStringForDatabase;
                }
            } catch (Exception e) {
                deleteFileAndCheckResult(file);
                if (e instanceof ServiceException) {
                    throw (ServiceException) e;
                }
                else {
                    throw new ServiceException("500", "Upload failed");
                }
            }
        }
    }

    @Override
    public List<JSONObject> uploadBatch(String requestInfoJsonString, List<MultipartFile> fileList) {
        if (backendHost.isBlank() || backendPort.isBlank() || backendApiToken.isBlank()) {
            throw new ServiceException("500", "This API is currently unavailable");
        }
        if (requestInfoJsonString == null) {
            throw new ServiceException("400", "This API can not be used with admin token");
        }
        JSONObject requestInfoJsonObject = JSONObject.parseObject(requestInfoJsonString);
        Map<String, String> fileInfoMap = requestInfoJsonObject.getObject("fileInfo", new TypeReference<>() {});
        if (fileList.size() != fileInfoMap.size()) {
            throw new ServiceException("400", "Invalid file number");
        }
        List<String> filePathStringForDatabaseList = new ArrayList<>();
        try {
            for (MultipartFile file : fileList) {
                String fileDir = fileInfoMap.get(file.getOriginalFilename());
                if (fileDir == null) {
                    ServiceException serviceException = new ServiceException("400", "Invalid file name");
                    serviceException.setCauseObject(file.getOriginalFilename());
                    throw serviceException;
                }
                try {
                    filePathStringForDatabaseList.add((String) upload(fileDir, file, false));
                } catch (ServiceException e) {
                    e.setCauseObject(file.getOriginalFilename());
                    throw e;
                }
            }
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            JSONObject requestBody = new JSONObject();
            requestBody.put("filePathList", new JSONArray(filePathStringForDatabaseList));
            requestBody.put("requestData", requestInfoJsonObject.getJSONObject("requestData"));
            httpHeaders.set("token", backendApiToken);
            try {
                HttpEntity<JSONObject> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
                ResponseEntity<JSONObject> responseFromBackend = restTemplate.exchange("http://" + backendHost + ":" + backendPort + "/attachment/uploadBatchFinish", HttpMethod.POST, httpEntity, JSONObject.class);
                if (responseFromBackend.getStatusCode().value() != 200) {
                    throw new ServiceException("500", "An error occurred when communicating with the backend");
                }
                else {
                    return Objects.requireNonNull(responseFromBackend.getBody()).getList("data", JSONObject.class);
                }
            } catch (RestClientException e) {
                throw new ServiceException("500", "An error occurred when communicating with the backend");
            }
        } catch (ServiceException e) {
            for (String filePathStringForDatabase : filePathStringForDatabaseList) {
                File file = new File(GlobalData.FILE_DIRECTORY, filePathStringForDatabase);
                deleteFileAndCheckResult(file);
            }
            throw e;
        }
    }

    private static void deleteFileAndCheckResult(@NotNull File file) {
        try {
            boolean ignore = file.delete();
            if (file.exists()) {
                log.error("Upload process failed and failed to delete the uploaded file due to unexpected error");
            }
        } catch (SecurityException ex) {
            log.error("Upload process failed and failed to delete the uploaded file due to a SecurityException");
        }
    }

    @Override
    public void deleteBatch(List<String> filePathList) {
        for (String filePath : filePathList) {
            try {
                delete(filePath);
            } catch (ServiceException e) {
                e.setCauseObject(filePath);
                throw e;
            }
        }
    }
}
