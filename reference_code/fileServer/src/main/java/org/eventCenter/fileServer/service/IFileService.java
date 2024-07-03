package org.eventCenter.fileServer.service;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    ResponseEntity<?> download(String filePath);

    void delete(String filePath);

    Object upload(String requestInfo, MultipartFile inputFile, boolean mode);

    List<JSONObject> uploadBatch(String backendData, List<MultipartFile> fileList);

    void deleteBatch(List<String> filePathList);
}
