package com.example.ordersystem.ordering.controller;


import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.dto.OrderResDto;
import com.example.ordersystem.ordering.service.OrderingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderCreateDto> dtos){
        Ordering ordering = orderingService.orderCreate(dtos);
        return new ResponseEntity<>(ordering.getId(), HttpStatus.CREATED);
    }
    @GetMapping("/list")
    public ResponseEntity<?> orderingList(){
        List<OrderResDto> dtos = orderingService.findAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderResDto> orderResDtos = orderingService.myOrders();
        return new ResponseEntity<>(orderResDtos, HttpStatus.OK );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.orderCancel(id);
        return new ResponseEntity<>(ordering.getId(), HttpStatus.OK);
    }



}
