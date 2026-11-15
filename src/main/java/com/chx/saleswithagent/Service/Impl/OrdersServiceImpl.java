package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.OrdersDTO;
import com.chx.saleswithagent.Entity.OrdersEntity;
import com.chx.saleswithagent.Entity.OrdersItemsEntity;
import com.chx.saleswithagent.Repository.OrdersRepository;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.OrdersService;
import com.chx.saleswithagent.VO.OrderItemsVO;
import com.chx.saleswithagent.VO.OrdersVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {
    @Resource
    private OrdersRepository ordersRepository;
    @Override
    public List<OrdersVO> getOrders(Long userId, String status) {
        List<OrdersEntity> orders = ordersRepository.getOrders(userId, status);
        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(OrdersEntity::getId).toList();
        Map<Long, List<OrdersItemsEntity>> itemMap =
                ordersRepository.getOrdersItems(orderIds);

        List<OrdersVO> result = new ArrayList<>(orders.size());

        for (OrdersEntity o : orders) {
            OrdersVO vo = new OrdersVO();
            BeanUtils.copyProperties(o, vo);   // 单对象复制，字段名要一致

            List<OrderItemsVO> items = new ArrayList<>();
            for (OrdersItemsEntity item : itemMap.getOrDefault(o.getId(), List.of())) {
                OrderItemsVO iv = new OrderItemsVO();
                BeanUtils.copyProperties(item, iv);
                items.add(iv);
            }
            vo.setItems(items);
            result.add(vo);
        }

        return result;
    }

    @Override
    public OrdersVO addOrders(OrdersDTO ordersDTO, Long userId) {
       return ordersRepository.addOrders(ordersDTO,userId);

    }
}
