package com.example.ordersystem.ordering.dto;


import com.example.ordersystem.ordering.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResDto {
    private Long id;
    private String memberEmail;
    private String orderStatus;
    private List<OrderDetailResDto> orderDetails;
}
