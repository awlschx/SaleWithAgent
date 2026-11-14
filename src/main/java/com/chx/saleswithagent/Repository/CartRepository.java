package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.DTO.AllSelectedDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.Entity.CartItemEntity;
import com.chx.saleswithagent.VO.CartItemVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class CartRepository {
    private final JdbcTemplate jdbc;

    public CartRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<CartItemEntity> ENTITY_MAPPER = (rs, i) -> {
        CartItemEntity e = new CartItemEntity();
        e.setId(rs.getLong("id"));
        e.setUserId(rs.getLong("user_id"));
        e.setProductId(rs.getLong("product_id"));
        e.setSpec(rs.getString("spec"));
        e.setQuantity(rs.getInt("quantity"));
        e.setSelected(rs.getBoolean("selected"));
        var ts = rs.getTimestamp("created_at");
        e.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return e;
    };
    /*
    *购物车添加商品
    *
    *
     */
    public int addOrMerge(Long userId, Long productId, String spec, Integer quantity, Boolean selected) {
        String sql = "INSERT INTO cart_items (user_id, product_id, spec, quantity, selected) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = LEAST(quantity + ?, 99)";
        return jdbc.update(sql, userId, productId, spec, quantity,
                selected == null ? Boolean.TRUE : selected, quantity);
    }
    /*
    *
    * 查询购物车商品返回
     */
    public List<CartItemVO> list(Long userId){
        String sql = "SELECT" +
                " c.id, c.product_id, c.spec, c.quantity, c.selected, p.name, p.price, p.price * c.quantity AS subtotal " +
                "FROM cart_items c JOIN products p ON p.id = c.product_id" +
                " WHERE c.user_id = ? " +
                "ORDER BY c.id";
        return jdbc.query(sql,
                (rs, i) -> {
                    CartItemVO v = new CartItemVO();
                    v.setId(rs.getLong("id"));
                    v.setProductId(rs.getLong("product_id"));
                    v.setName(rs.getString("name"));
                    v.setSpec(rs.getString("spec"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setQuantity(rs.getInt("quantity"));
                    v.setSelected(rs.getBoolean("selected"));
                    v.setSubtotal(v.getPrice().multiply(BigDecimal.valueOf(v.getQuantity())));
                    return v;
                }
                ,userId);
    }
    /*
    *更新购物车数据，数量加减，勾选状态
    *
    *
     */
    public int updateItem(Long userId,Long itemId, CartItemUpdateDTO cartItemUpdateDTO){
        String sql = "UPDATE cart_items SET quantity = ?, selected = ? WHERE cart_items.id = ? and user_id = ?";

        return jdbc.update(sql,cartItemUpdateDTO.getQuantity(),cartItemUpdateDTO.getSelected(),itemId,userId);
    }

    /*
    *删除购物车数据
    *
     */
    public  int deleteItem(Long userId,Long itemId){
        String sql = "DELETE FROM cart_items WHERE user_id = ? and id = ?";
        return jdbc.update( sql,userId,itemId);
    }
    /*
    *返回购物车的商品条数
    *
     */
    public int count(Long userId){
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        return jdbc.queryForObject(sql, Integer.class,userId);
    }
    /*
    *全勾选商品
    *
     */
    public int allSelected(Long userId, AllSelectedDTO  allSelectedDTO) {
        String sql = "update cart_items set selected = ? where user_id = ?";
        return jdbc.update(sql,allSelectedDTO.getSelected(),userId);
    }
    /*
    *删除选中商品
    *
     */
    public int deleteSelected(Long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ? AND selected = 1";
        return jdbc.update(sql,userId);
    }
}
