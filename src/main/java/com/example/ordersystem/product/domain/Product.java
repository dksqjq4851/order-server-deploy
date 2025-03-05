package com.example.ordersystem.product.domain;

import com.example.ordersystem.common.domain.BaseTimeEntity;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.product.dto.ProductResDto;
import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public ProductResDto fromEntity(){
        return ProductResDto.builder()
                .id(this.id).name(this.name).price(this.price).stockQuantity(this.stockQuantity).category(this.category)
                .imagePath(this.imagePath)
                .build();
    }
    public void updateImagePath(String imagePath){
        this.imagePath = imagePath;
    }
    public void updateStockQuantity(Integer stockQuantity){
        this.stockQuantity = this.stockQuantity - stockQuantity;
    }
    public void canceledQuantity(Integer stockQuantity){
        this.stockQuantity = this.stockQuantity + stockQuantity;
    }
}
