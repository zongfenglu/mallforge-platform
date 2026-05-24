package com.mallforge.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class PrepayResponse {
    private String channel;
    /** 前端发起支付所需的全部参数 */
    private Map<String, Object> params;
}
