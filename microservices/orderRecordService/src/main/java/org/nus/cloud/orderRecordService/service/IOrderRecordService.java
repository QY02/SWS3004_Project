package org.nus.cloud.orderRecordService.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.nus.cloud.orderRecordService.entity.OrderRecord;

import java.util.List;

public interface IOrderRecordService extends IService<OrderRecord> {

    List<OrderRecord> get(HttpServletRequest request, JSONObject requestData);

    OrderRecord add(OrderRecord orderRecord);
}