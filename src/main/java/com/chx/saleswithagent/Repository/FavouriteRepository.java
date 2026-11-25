package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.VO.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class FavouriteRepository {
    private final JdbcTemplate jdbc;

    public FavouriteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ProductsVO> getFavorite(Long userId) {
        //从favourite表里面取出所有行
        String sql = "SELECT id,name,category,price,original_price,sales,stock FROM products " +
                " WHERE id in (SELECT product_id FROM favorites WHERE user_id = ?)";
        return jdbc.query(sql,
                (rs,i) -> {
            ProductsVO productsVO = new ProductsVO();
            productsVO.setId(rs.getInt("id"));
            productsVO.setName(rs.getString("name"));
            productsVO.setCategory(rs.getString("category"));
            productsVO.setPrice(rs.getBigDecimal("price"));
            productsVO.setOriginalPrice(rs.getBigDecimal("original_price"));
            productsVO.setSales(rs.getInt("sales"));
            productsVO.setStock(rs.getInt("stock"));
            return productsVO;
                }, userId);
    }

    public Boolean toggleFavorite(Long userId, Long productId) {
        //写入favourite表
        int deleted = jdbc.update(
                "DELETE FROM favorites WHERE user_id = ? AND product_id = ?",
                userId, productId);

        if (deleted > 0) {
            return false;   // 原来有 → 这次是取消收藏
        }

        jdbc.update(
                "INSERT IGNORE INTO favorites (user_id, product_id) VALUES (?, ?)",
                userId, productId);

        return true;
    }
}
