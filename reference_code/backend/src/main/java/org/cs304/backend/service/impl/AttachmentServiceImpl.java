package org.cs304.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.cs304.backend.constant.constant_User;
import org.cs304.backend.entity.Attachment;
import org.cs304.backend.exception.ServiceException;
import org.cs304.backend.mapper.AttachmentMapper;
import org.cs304.backend.service.IAttachmentService;
import org.cs304.backend.utils.RedisUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    @Resource
    private RedisUtil redisUtil;

    @Value("${file-server.admin-token:}")
    private String adminToken;

    @Value("${file-server.host:}")
    private String fileServerHost;

    @Value("${file-server.port:}")
    private String fileServerPort;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Attachment getById(int userType, Integer id) {
        if (id == null) {
            throw new ServiceException("400", "Invalid Attachment Id");
        }
        if (userType != constant_User.ADMIN) {
            throw new ServiceException("401", "Permission denied");
        }
        Attachment attachment = baseMapper.selectById(id);
        if (attachment == null) {
            throw new ServiceException("400", "Attachment not exist");
        }
        attachment.setFilePath(redisUtil.generateAndAddFileToken(attachment.getFilePath()));
        return attachment;
    }

    @Override
    public List<Attachment> getBatchByIds(int userType, List<Integer> idList) {
        List<Attachment> result = new ArrayList<>();
        for (Integer id : idList) {
            try {
                result.add(getById(userType, id));
            } catch (ServiceException e) {
                e.setCauseObject(id);
                throw e;
            }
        }
        return result;
    }

    @Override
    public void deleteById(int userType, Integer id) {
        if (userType != constant_User.ADMIN) {
            throw new ServiceException("401", "Permission denied");
        }
        if (fileServerHost.isBlank() || fileServerPort.isBlank() || adminToken.isBlank()) {
            throw new ServiceException("500", "This API is currently unavailable");
        }
        if (id == null) {
            throw new ServiceException("400", "Invalid Attachment Id");
        }
        Attachment attachment = baseMapper.selectById(id);
        if (attachment == null) {
            throw new ServiceException("400", "Attachment not exist");
        }
        baseMapper.deleteById(id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject requestBody = new JSONObject();
        requestBody.put("filePath", attachment.getFilePath());
        httpHeaders.set("token", adminToken);
        try {
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
            int statusCode = restTemplate.exchange("http://" + fileServerHost + ":" + fileServerPort + "/file/delete", HttpMethod.POST, httpEntity, Void.class).getStatusCode().value();
            if (statusCode != 200) {
                throw new ServiceException("500", "An error occurred when communicating with the file server");
            }
        } catch (RestClientException e) {
            throw new ServiceException("500", "An error occurred when communicating with the file server");
        }
    }

    @Override
    public JSONObject uploadStart(int userType, String fileDir, JSONObject requestData) {
        if (fileDir == null) {
            throw new ServiceException("400", "Invalid file path");
        }
        try {
            Paths.get(fileDir);
        } catch (InvalidPathException e) {
            throw new ServiceException("400", "Invalid file path");
        }

        JSONObject requestInfoJsonObject = new JSONObject();
        requestInfoJsonObject.put("fileDir", fileDir);
        if (requestData != null) {
            requestInfoJsonObject.put("requestData", requestData);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileToken", redisUtil.generateAndAddFileToken(requestInfoJsonObject.toJSONString()));
        return jsonObject;
    }

    @Override
    public JSONObject uploadFinish(int userType, String filePath, JSONObject requestData, boolean completeFileInfo) {
        if (filePath == null) {
            throw new ServiceException("400", "Invalid file path");
        }
        try {
            Paths.get(filePath);
        } catch (InvalidPathException e) {
            throw new ServiceException("400", "Invalid file path");
        }
        if (userType != constant_User.FILE_SERVER) {
            throw new ServiceException("401", "Permission denied");
        }
        Attachment attachment = new Attachment();
        attachment.setFilePath(filePath);
        baseMapper.insert(attachment);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("attachmentId", attachment.getId());
        if (completeFileInfo) {
            jsonObject.put("attachmentInfo", attachment);
        }
        if (requestData != null) {
            requestData.put("fileInfo", attachment);
            try {
                Object service = applicationContext.getBean(Class.forName((String) requestData.get("serviceClassName")));
                Method method = service.getClass().getMethod((String) requestData.get("serviceMethodName"), JSONObject.class);
                jsonObject.put("requestResult", JSONObject.from(method.invoke(service, requestData)));
            } catch (NoSuchMethodException e) {
                throw new ServiceException("500", "Cannot invoke the corresponding method");
            } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject uploadBatchStart(int userType, List<String> fileDirList, List<String> fileNameList, JSONObject requestData) {
        if (userType != constant_User.ADMIN) {
            throw new ServiceException("401", "Permission denied");
        }
        if ((fileDirList == null) || (fileNameList == null) || fileDirList.isEmpty() || fileNameList.isEmpty() || fileDirList.size() != fileNameList.size()) {
            throw new ServiceException("400", "Invalid parameters");
        }
        int nullIndex = fileDirList.indexOf(null);
        if (nullIndex != -1) {
            throw new ServiceException("400", "Invalid value in fileDirList at index " + nullIndex);
        }
        nullIndex = fileNameList.indexOf(null);
        if (nullIndex != -1) {
            throw new ServiceException("400", "Invalid value in fileNameList at index " + nullIndex);
        }
        JSONObject requestInfoJsonObject = new JSONObject();
        JSONObject fileInfoJsonObject = new JSONObject();
        for (int i = 0; i < fileDirList.size(); i++) {
            try {
                Paths.get(fileDirList.get(i), fileNameList.get(i));
            } catch (InvalidPathException e) {
                throw new ServiceException("400", "Invalid parameters");
            }
            fileInfoJsonObject.put(fileNameList.get(i), fileDirList.get(i));
        }
        requestInfoJsonObject.put("fileInfo", fileInfoJsonObject);
        if (requestData != null) {
            requestInfoJsonObject.put("requestData", requestData);
        }
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("fileToken", redisUtil.generateAndAddFileToken(requestInfoJsonObject.toJSONString()));
        return resultJsonObject;
    }

    @Override
    public JSONObject uploadBatchFinish(int userType, List<String> filePathList, JSONObject requestData) {
        if (filePathList == null) {
            throw new ServiceException("400", "Invalid file path list");
        }
        List<JSONObject> fileInfoList = new ArrayList<>();
        for (String filePath : filePathList) {
            try {
                fileInfoList.add(((IAttachmentService) AopContext.currentProxy()).uploadFinish(userType, filePath, null, true));
            } catch (ServiceException e) {
                e.setCauseObject(filePath);
                throw e;
            }
        }
        JSONObject result = new JSONObject();
        if (requestData != null) {
            requestData.put("fileInfoList", fileInfoList.stream().map(jsonObject -> jsonObject.getObject("attachmentInfo", Attachment.class)).collect(Collectors.toList()));
            try {
                Object service = applicationContext.getBean(Class.forName((String) requestData.get("serviceClassName")));
                Method method = service.getClass().getMethod((String) requestData.get("serviceMethodName"), JSONObject.class);
                result.put("requestResult", JSONObject.from(method.invoke(service, requestData)));
            } catch (NoSuchMethodException e) {
                throw new ServiceException("500", "Cannot invoke the corresponding method");
            } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        result.put("fileInfoList", fileInfoList.stream().peek(jsonObject -> jsonObject.remove("attachmentInfo")).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void deleteBatchByIdList(int userType, List<Integer> idList) {
        if (userType != constant_User.ADMIN) {
            throw new ServiceException("401", "Permission denied");
        }
        if (fileServerHost.isBlank() || fileServerPort.isBlank() || adminToken.isBlank()) {
            throw new ServiceException("500", "This API is currently unavailable");
        }
        if ((idList == null) || (idList.isEmpty())) {
            throw new ServiceException("400", "Invalid id list");
        }
        int nullIndex = idList.indexOf(null);
        if (nullIndex != -1) {
            throw new ServiceException("400", "Invalid id at index " + nullIndex);
        }
        List<String> filePathList = idList.stream().map(id -> {
            Attachment attachment = baseMapper.selectById(id);
            if (attachment == null) {
                throw new ServiceException("400", "Attachment with id = " + id + " not exist");
            }
            return attachment.getFilePath();
        }).collect(Collectors.toList());
        baseMapper.deleteBatchIds(idList);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject requestBody = new JSONObject();
        requestBody.put("filePathList", new JSONArray(filePathList));
        httpHeaders.set("token", adminToken);
        try {
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
            int statusCode = restTemplate.exchange("http://" + fileServerHost + ":" + fileServerPort + "/file/deleteBatch", HttpMethod.POST, httpEntity, Void.class).getStatusCode().value();
            if (statusCode != 200) {
                throw new ServiceException("500", "An error occurred when communicating with the file server");
            }
        } catch (RestClientException e) {
            throw new ServiceException("500", "An error occurred when communicating with the file server");
        }
    }
}
