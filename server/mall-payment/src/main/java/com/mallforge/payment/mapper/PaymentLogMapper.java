package com.mallforge.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mallforge.payment.entity.PaymentLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PaymentLogMapper extends BaseMapper<PaymentLog> {

    @Update("UPDATE payment_log SET status = #{status}, transaction_no = #{transactionNo} " +
            "WHERE order_id = (SELECT id FROM `order` WHERE order_no = #{orderNo} LIMIT 1)")
    int updateStatusByOrderNo(String orderNo, String status, String transactionNo);
}
