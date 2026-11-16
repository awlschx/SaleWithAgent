package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.DTO.OrdersDTO;
import com.chx.saleswithagent.DTO.OrdersItemsDTO;
import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Entity.CartItemEntity;
import com.chx.saleswithagent.Entity.OrdersEntity;
import com.chx.saleswithagent.Entity.OrdersItemsEntity;
import com.chx.saleswithagent.Service.Impl.CartServiceImpl;
import com.chx.saleswithagent.VO.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.servlet.filter.OrderedFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class OrdersRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;
    @Resource
    private CouponsRepository couponsRepository;
    @Resource
    private AddressRepository addressRepository;

    public OrdersRepository(JdbcTemplate jdbc,
                            ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }



    /*
    *获取每个用户的全部订单详细
    *
     */
    public List<OrdersEntity> getOrders(Long userId,String status) {
        StringBuilder sql = new StringBuilder("SELECT id, order_no, user_id, status, item_amount, ship_fee, discount," +
                "pay_amount, address_snapshot, user_coupon_id, " +
                "created_at, ship_time, receive_time " +
                "FROM orders " +
                "WHERE user_id = ? ");
        List<Object> args = new ArrayList<>();
        args.add(userId);
        if(status != null && !status.equalsIgnoreCase("all")
                && !status.equals("全部") && !status.isBlank()){
            sql.append(" AND status =  ");
            args.add(status);
        }
        sql.append(" ORDER BY created_at DESC");
        log.info("getOrders{}",sql);
        List<OrdersEntity> ordersEntities = new ArrayList<>();
        ordersEntities = jdbc.query(sql.toString(),
                (rs,i) ->{
                    OrdersEntity e = new OrdersEntity();
                    e.setId(rs.getLong("id"));
                    e.setOrderNo(rs.getString("order_no"));
                    e.setUserId(rs.getLong(("user_id")));
                    e.setStatus(rs.getString("status"));
                    e.setItemAmount(rs.getBigDecimal("item_amount"));
                    e.setShipFee(rs.getBigDecimal("ship_fee"));
                    e.setDiscount(rs.getBigDecimal("discount"));
                    e.setPayAmount(rs.getBigDecimal("pay_amount"));
                    String json = rs.getString("address_snapshot");
                    e.setAddressSnapshot(json == null ? null
                            : objectMapper.readValue(json, AddressVO.class));

                    e.setUserCouponId(rs.getLong("user_coupon_id"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if(createdAt == null){
                        e.setCreatedAt(null);
                    }
                    else {
                        e.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    Timestamp shipTime = rs.getTimestamp("ship_time");
                    if(shipTime == null){
                        e.setShipTime(null);
                    }
                    else{
                        e.setShipTime(shipTime.toLocalDateTime());
                    }
                    Timestamp receiveTime = rs.getTimestamp("receive_time");
                    if(receiveTime == null){
                        e.setReceiveTime(null);
                    }
                    else{
                        e.setReceiveTime(receiveTime.toLocalDateTime());
                    }

        return e;},args.toArray());
        log.info("getOrders{}",ordersEntities);
        return ordersEntities;
    }
    /*
    *根据订单号获取订单（还不包含items）
    *
     */
    public OrdersEntity getOrderById(String orderNo) {
        String sql = "SELECT id, order_no, user_id, status, item_amount, ship_fee, discount," +
                "pay_amount, address_snapshot, user_coupon_id, " +
                "created_at, ship_time, receive_time " +
                "FROM orders " +
                "WHERE order_no = ?";
        return  jdbc.queryForObject(sql,
                (rs,i) ->{
                    OrdersEntity e = new OrdersEntity();
                    e.setId(rs.getLong("id"));
                    e.setOrderNo(rs.getString("order_no"));
                    e.setUserId(rs.getLong(("user_id")));
                    e.setStatus(rs.getString("status"));
                    e.setItemAmount(rs.getBigDecimal("item_amount"));
                    e.setShipFee(rs.getBigDecimal("ship_fee"));
                    e.setDiscount(rs.getBigDecimal("discount"));
                    e.setPayAmount(rs.getBigDecimal("pay_amount"));
                    String json = rs.getString("address_snapshot");
                    e.setAddressSnapshot(json == null ? null
                            : objectMapper.readValue(json, AddressVO.class));

                    e.setUserCouponId(rs.getLong("user_coupon_id"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if(createdAt == null){
                        e.setCreatedAt(null);
                    }
                    else {
                        e.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    Timestamp shipTime = rs.getTimestamp("ship_time");
                    if(shipTime == null){
                        e.setShipTime(null);
                    }
                    else{
                        e.setShipTime(shipTime.toLocalDateTime());
                    }
                    Timestamp receiveTime = rs.getTimestamp("receive_time");
                    if(receiveTime == null){
                        e.setReceiveTime(null);
                    }
                    else{
                        e.setReceiveTime(receiveTime.toLocalDateTime());
                    }

                    return e;},orderNo);
    }
    /*
    *获取每个订单的详细
    *
     */
    public Map<Long, List<OrdersItemsEntity>> getOrdersItems(List<Long> orderIds){
        Map<Long, List<OrdersItemsEntity>> itemMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            String placeholders = String.join(",", Collections.nCopies(orderIds.size(), "?"));
            String sql = "SELECT order_id, product_id, product_name, spec, price, quantity " +
                    "FROM order_items WHERE order_id IN (" + placeholders + ") ORDER BY id";
            jdbc.query(sql, rs -> {
                OrdersItemsEntity item = new OrdersItemsEntity();
                item.setProductId(rs.getLong("product_id"));
                item.setSpec(rs.getString("spec"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                long orderId = rs.getLong("order_id");
                itemMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(item);
            }, orderIds.toArray());
        }
        return itemMap;
    }

    /*
    *添加订单
    *
     */
    @Transactional
    public OrdersVO addOrders(OrdersDTO ordersDTO, Long userId) {
        String add_sql = "INSERT INTO orders (order_no, user_id, status, " +
                "item_amount, ship_fee, discount,pay_amount," +
                " address_snapshot, user_coupon_id)" +
                " VALUE (?,?,?,?,?,?,?,?,?)";
        String product_sql = "SELECT price FROM products WHERE id = ?";
        BigDecimal total_price = BigDecimal.ZERO;
        //计算总金额，运费，优惠金额，实付
        List<OrdersItemsDTO> ordersItems = ordersDTO.getItems();
        for (OrdersItemsDTO ordersItem : ordersItems) {
            Long productId = ordersItem.getProductId();
            int quantity = ordersItem.getQuantity();
            BigDecimal price = jdbc.queryForObject(product_sql, BigDecimal.class, productId);
            total_price = total_price.add(price.multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_DOWN));
        }

        BigDecimal shipPrice = BigDecimal.ZERO;
        BigDecimal minAmount = BigDecimal.ZERO;
        BigDecimal offAmount = BigDecimal.ZERO;
        BigDecimal actualPay = BigDecimal.ZERO;
        actualPay = actualPay.add(total_price);
        log.info("couponId{}",String.valueOf(ordersDTO.getCouponId()));
        if(ordersDTO.getCouponId()!=null){
            //查询优惠卷
            UserCouponsVO userCouponsVO = couponsRepository.queryUserCouponById(userId, ordersDTO.getCouponId());
            //删除优惠卷
            couponsRepository.deleteCoupons(ordersDTO.getCouponId());
            //校验
            if (userCouponsVO == null || userCouponsVO.getUsed() == true) {
                throw new RuntimeException();
            }
             minAmount = userCouponsVO.getMinAmount();
             offAmount = userCouponsVO.getOffAmount();
             if (total_price.compareTo(minAmount) >= 0) {
                 actualPay = total_price.subtract(offAmount);
            }

        }

        log.info("actualPay{}",String.valueOf(actualPay));
        log.info("total_price{}",String.valueOf(total_price));

        //获取收货地址
        Long addressId = ordersDTO.getAddressId();
        AddressEntity addressEntity = addressRepository.getAddressById(addressId);
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressEntity, addressVO);
        //把对象序列化
        String address = objectMapper.writeValueAsString(addressVO);
        //生成订单号
        String orderNo = "SG"+LocalDateTime.now().toString().replace("-", "");
        //执行sql语句
        jdbc.update(add_sql, orderNo, userId, "待发货",total_price,
                shipPrice, offAmount, actualPay, address, ordersDTO.getCouponId());
        //把item插入orderitems
        String item_sql = "INSERT INTO order_items (order_id,product_id,product_name,spec,price,quantity)" +
                " VALUES ((SELECT id FROM orders WHERE order_no = ? ),?,(SELECT name FROM products WHERE id = ?),?,(SELECT price FROM products WHERE id = ?),?)";
        for(OrdersItemsDTO ordersItem : ordersItems){
            Long productId = ordersItem.getProductId();
            int quantity = ordersItem.getQuantity();
            String spec = ordersItem.getSpec();
            jdbc.update(item_sql,orderNo,productId,productId,spec,productId,quantity);
        }
        //根据订单号获取该订单再返回前端
        OrdersEntity orderEntityById = getOrderById(orderNo);
        Long orderId = orderEntityById.getId();
        //log.info(orderEntityById.toString());
        Map<Long, List<OrdersItemsEntity>> ordersItems1 = getOrdersItems(Collections.singletonList(orderId));
        //log.info("ordersItems1: " + ordersItems1.toString());
        OrdersVO vo = new OrdersVO();

        BeanUtils.copyProperties(orderEntityById, vo);   // 单对象复制，字段名要一致
        //log.info(vo.toString());
        List<OrderItemsVO> items = new ArrayList<>();
        List<OrdersItemsEntity> orDefault = ordersItems1.getOrDefault(orderEntityById.getId(), List.of());
        //log.info("orDefault{}", orDefault.toString());
        for (OrdersItemsEntity item : orDefault) {
            OrderItemsVO iv = new OrderItemsVO();
            //log.info(item.toString());

            BeanUtils.copyProperties(item, iv);
            //log.info(iv.toString());
            items.add(iv);
        }
        //log.info(items.toString());
        vo.setItems(items);
        if ("cart".equals(ordersDTO.getSource())) {
            String deleteCartSql =
                    "DELETE FROM cart_items WHERE user_id = ? AND product_id = ? AND spec = ?";

            List<Object[]> args = ordersDTO.getItems().stream()
                    .map(i -> new Object[]{ userId, i.getProductId(), i.getSpec() })
                    .toList();

            jdbc.batchUpdate(deleteCartSql, args);
        }

        return vo;
    }
    /*
    *根据订单号获取可以返回前端的订单（包含items）
     */
    public OrdersVO getTrueOrdersVO(String orderNo){
        OrdersEntity ordersEntity = getOrderById(orderNo);
        OrdersVO vo = new OrdersVO();
        BeanUtils.copyProperties(ordersEntity, vo);
        //获取items
        Map<Long, List<OrdersItemsEntity>> ordersItems
                = getOrdersItems(Collections.singletonList(ordersEntity.getId()));
        List<OrderItemsVO>  items = new ArrayList<>();
        //获取每个订单的商品集合
        List<OrdersItemsEntity> orDefault =
                ordersItems.getOrDefault(ordersEntity.getId(), List.of());
        for (OrdersItemsEntity item : orDefault) {
            OrderItemsVO iv = new OrderItemsVO();
            BeanUtils.copyProperties(item, iv);
            items.add(iv);

        }
        vo.setItems(items);
        return vo;


    }


    /*
    *取消订单
    *
     */
   public OrdersVO cancelOrders(String orderNo) {
        String order_sql = "UPDATE orders SET status = '已取消' WHERE order_no = ?";
        String item_sql = "DELETE FROM order_items WHERE order_id = (SELECT id FROM orders WHERE order_no = ?)";
        jdbc.update(order_sql, orderNo);
       OrdersVO trueOrdersVO = getTrueOrdersVO(orderNo);
       jdbc.update(item_sql, orderNo);
        return trueOrdersVO;
   }

   /*
   *模拟发货
   *
    */
    public OrdersVO shipOrders(String orderNo) {
        String sql = "UPDATE orders SET status = '待收货' ,ship_time = NOW() WHERE order_no = ?";
        jdbc.update(sql,orderNo);
        return getTrueOrdersVO(orderNo);

    }

    /*
    *确认收货
    *
     */
    public OrdersVO receiveOrders(String orderNo) {
        String sql = "UPDATE orders SET status = '已完成' ,receive_time = NOW() WHERE order_no = ?";
        jdbc.update(sql,orderNo);
        return getTrueOrdersVO(orderNo);
    }

}
