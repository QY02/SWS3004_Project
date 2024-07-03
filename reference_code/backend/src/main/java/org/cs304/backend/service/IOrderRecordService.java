package org.cs304.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cs304.backend.entity.OrderRecord;

public interface IOrderRecordService extends IService<OrderRecord> {

    Object getMyOrderRecord(String userId, Integer eventId, Integer mode);

    Object getUnpaidOrderRecord(String userId, Integer eventId, Integer mode);

    int getPaymentById(Integer orderId);

    Object getEventOrderRecord(Integer eventId);
}
