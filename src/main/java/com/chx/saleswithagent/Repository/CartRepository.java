package com.chx.saleswithagent.Repository;

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
        e.setId(rs.getLong("product_id"));
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
    public int addOrMerge(Long userid, Long productId,String spec, Integer quantity,Boolean selected) {
        String sql = "INSERT INTO cart_items (user_id, product_id,spec, quantity,selected) VALUES (?, ?, ?, ?,?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        return jdbc.update(sql, userid, productId,spec, quantity,selected);
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
                } ,userId);
    }
}
