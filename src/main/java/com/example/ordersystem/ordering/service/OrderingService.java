package com.example.ordersystem.ordering.service;

import com.example.ordersystem.common.service.StockInvetoryService;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.OrderDetail;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.dto.OrderResDto;
import com.example.ordersystem.ordering.repository.OrderingDetailRepository;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final MemberRepository memberRepository;
    private final OrderingDetailRepository orderingDetailRepository;
    private final OrderingRepository orderingRepository;
    private final ProductRepository productRepository;
    private final StockInvetoryService stockInvetoryService;

    public OrderingService(MemberRepository memberRepository, OrderingDetailRepository orderingDetailRepository, OrderingRepository orderingRepository, ProductRepository productRepository, StockInvetoryService stockInvetoryService) {
        this.memberRepository = memberRepository;
        this.orderingDetailRepository = orderingDetailRepository;
        this.orderingRepository = orderingRepository;
        this.productRepository = productRepository;
        this.stockInvetoryService = stockInvetoryService;
    }
    public Ordering orderCancel(Long id){
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("order is not found"));
        ordering.cancle();
        List<OrderDetail> orderDetails = ordering.getOrderDetails();
        for(OrderDetail od : orderDetails){
            Product p = od.getProduct();
            p.canceledQuantity(od.getQuantity());
        }
        return ordering;

    }


    public List<OrderResDto> myOrders(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member =memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("memeber is not found"));
        List<Ordering> orderings = member.getOrderingList();
        List<OrderResDto> orderListResDtos = new ArrayList<>();
        for (Ordering o : orderings){
            orderListResDtos.add(o.fromEntity());
        }
        return orderListResDtos;
        }

    public List<OrderResDto> findAll(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderResDto> orderListResDtos = new ArrayList<>();
        for (Ordering o : orderingRepository.findAll()){
            orderListResDtos.add(o.fromEntity());
        }
        return orderListResDtos;
    }

    public Ordering orderCreate(List<OrderCreateDto> dtos){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
//        방법2. cascading 사용하여 db저장
//        Ordering객체 생성하면서 OrderingDetail객체 같이 생성
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        for (OrderCreateDto o : dtos){
            Product product = productRepository.findById(o.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));
            int quantity = o.getProductCount();
//            동시성 이슈가 고려안된 코드
            if (product.getStockQuantity()<quantity){
                throw new IllegalArgumentException("재고 부족");
            }else {
//                재고감소 로직
                product.updateStockQuantity(o.getProductCount());
            }

//            동시성 이슈를 고려한 코드
//            redis를 통한 재고관리 및 재고 잔량 확인.
//            int newQuantity = stockInvetoryService.decreaseStock(product.getId(), quantity);
//            if (newQuantity<0){
//                throw new IllegalArgumentException("재고부족");
//            }
////            rdb동기화(rabbitmq)
//            StockRabbitDto dto = StockRabbitDto.builder()
//                    .productId(product.getId())
//                    .productCount(quantity)
//                    .build();
//            stockRabbitmqService.publish(dto);



            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .product(product)
                    .quantity(o.getProductCount())
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        orderingRepository.save(ordering);
        return ordering;
    }
    }



