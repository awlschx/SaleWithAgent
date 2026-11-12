package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.Entity.CartItemEntity;
import com.chx.saleswithagent.Entity.ProductsEntity;
import com.chx.saleswithagent.VO.CartItemVO;
import com.chx.saleswithagent.VO.ProductsVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ProductsRepository {
    //注入jdbc
    private final JdbcTemplate jdbc;
    public ProductsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    /*
    *和数据库表做映射
    *
     */
    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<ProductsEntity> ENTITY_MAPPER = (rs, i) -> {
        ProductsEntity e = new ProductsEntity();
        e.setId(rs.getInt("id"));
        e.setProductName(rs.getString("name"));
        e.setPrice(rs.getFloat("price"));
        e.setCategory(rs.getString("category"));
        e.setOriginalPrice(rs.getFloat("original_price"));
        e.setSales(rs.getInt("sales"));
        e.setStock(rs.getInt("stock"));
        var ts1 = rs.getTimestamp("created_at");
        var ts2 = rs.getTimestamp("updated_at");
        e.setCreateTime(ts1 == null ? null : ts1.toLocalDateTime());
        e.setUpdateTime(ts2 == null ? null : ts2.toLocalDateTime());
        return e;
    };

    /*
    *商品获取展示
    *
     */
    public List<ProductsVO> ListProducts(){

        String sql = "SELECT * FROM products";
        return jdbc.query(sql,
                (rs, i) -> {
                    ProductsVO v = new ProductsVO();
                    v.setId(rs.getInt("id"));
                    v.setProductName(rs.getString("name"));
                    v.setPrice(rs.getFloat("price"));
                    v.setCategory(rs.getString("category"));
                    v.setOriginalPrice(rs.getFloat("original_price"));
                    v.setSales(rs.getInt("sales"));
                    v.setStock(rs.getInt("stock"));
                    var ts1 = rs.getTimestamp("created_at");
                    var ts2 = rs.getTimestamp("updated_at");
                    v.setCreateTime(ts1 == null ? null : ts1.toLocalDateTime());
                    v.setUpdateTime(ts2 == null ? null : ts2.toLocalDateTime());
                    return v;
                }
                );
    }
}
