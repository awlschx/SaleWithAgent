package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.OrdersDTO;
import com.chx.saleswithagent.Entity.OrdersEntity;
import com.chx.saleswithagent.Entity.OrdersItemsEntity;
import com.chx.saleswithagent.Repository.OrdersRepository;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.OrdersService;
import com.chx.saleswithagent.VO.OrderItemsVO;
import com.chx.saleswithagent.VO.OrderPreviewVO;
import com.chx.saleswithagent.VO.OrdersVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {
    @Resource
    private OrdersRepository ordersRepository;
    @Override
    @Transactional
    public List<OrdersVO> getOrders(Long userId, String status) {
        log.info("status{}",status);
        List<OrdersEntity> orders = ordersRepository.getOrders(userId, status);
        log.info("orders{}",orders);
        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(OrdersEntity::getId).toList();
        log.info("orderIds:{}",orderIds);
        Map<Long, List<OrdersItemsEntity>> itemMap =
                ordersRepository.getOrdersItems(orderIds);
        log.info("itemMap:{}",itemMap);
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
    @Transactional
    public OrdersVO addOrders(OrdersDTO ordersDTO, Long userId) {
       return ordersRepository.addOrders(ordersDTO,userId);

    }

    @Override
    @Transactional
    public OrdersVO cancelOrders(String orderNo) {
        return ordersRepository.cancelOrders(orderNo);
    }

    @Override
    @Transactional
    public OrdersVO shipOrders(String orderNo) {
        //模拟发货要改orders表，把status改为已发货
        log.info("准备发货");
        return ordersRepository.shipOrders(orderNo);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPreviewVO previewOrders(OrdersDTO ordersDTO, Long userId) {
        log.info("预结算订单");
        return ordersRepository.previewOrders(ordersDTO, userId);
    }

    @Override
    @Transactional
    public OrdersVO receiveOrders(String orderNo) {
        log.info("确认收货");
        return ordersRepository.receiveOrders(orderNo);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersVO getOrderDetail(String orderNo, Long userId) {
        return ordersRepository.getOrderDetail(orderNo, userId);
    }
}
