package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.OrdersDTO;
import com.chx.saleswithagent.VO.OrdersVO;

import java.util.List;

public interface OrdersService {
     List<OrdersVO> getOrders(Long userId,String status);

     OrdersVO addOrders(OrdersDTO ordersDTO, Long userId);

    OrdersVO cancelOrders(String orderNo);

    OrdersVO shipOrders(String orderNo);

    OrdersVO receiveOrders(String orderNo);
}
