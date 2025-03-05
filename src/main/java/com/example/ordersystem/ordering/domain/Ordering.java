package com.example.ordersystem.ordering.domain;


import com.example.ordersystem.common.domain.BaseTimeEntity;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.ordering.dto.OrderDetailResDto;
import com.example.ordersystem.ordering.dto.OrderResDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Ordering extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void cancle(){
        this.orderStatus =OrderStatus.CANCELED;
    }


    public OrderResDto fromEntity(){
        List<OrderDetailResDto> dtos = new ArrayList<>();
        for (OrderDetail d : this.getOrderDetails()){
                OrderDetailResDto orderDetailResDto = OrderDetailResDto.builder()
                        .detailId(d.getId())
                        .count(d.getQuantity())
                        .productName(d.getProduct().getName())
                        .build();
                dtos.add(orderDetailResDto);

        }
        OrderResDto orderResDto = OrderResDto.builder()
                .id(this.getId())
                .memberEmail(this.getMember().getEmail())
                .orderStatus(String.valueOf(this.getOrderStatus()))
                .orderDetails(dtos)
                .build();
        return orderResDto;
    }
}
