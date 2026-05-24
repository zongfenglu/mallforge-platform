package com.mallforge.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal freight;
    private BigDecimal discountAmount;
    private Long couponId;
    private String remark;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Long skuId;
        private String productName;
        private String productImage;
        private String specDesc;
        private BigDecimal price;
        private Integer quantity;
    }
}
