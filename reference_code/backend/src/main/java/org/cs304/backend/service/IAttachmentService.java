package org.cs304.backend.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.Attachment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IAttachmentService extends IService<Attachment> {

    Attachment getById(int userType, Integer id);

    List<Attachment> getBatchByIds(int userType, List<Integer> idList);

    @Transactional(rollbackFor = {Exception.class})
    void deleteById(int userType, Integer id);

    JSONObject uploadStart(int userType, String fileDir, JSONObject requestData);

    @Transactional(rollbackFor = {Exception.class})
    JSONObject uploadFinish(int userType, String filePath, JSONObject requestData, boolean completeFileInfo);

    JSONObject uploadBatchStart(int userType, List<String> fileDirList, List<String> fileNameList, JSONObject requestData);

    @Transactional(rollbackFor = {Exception.class})
    JSONObject uploadBatchFinish(int userType, List<String> filePathList, JSONObject requestData);

    @Transactional(rollbackFor = {Exception.class})
    void deleteBatchByIdList(int userType, List<Integer> idList);
}
