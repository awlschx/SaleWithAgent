package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
import com.chx.saleswithagent.Entity.UserCouponsEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
public class CouponsRepository {
    private JdbcTemplate jdbc;
    public CouponsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<CouponsTemplateEntity> ENTITY_MAPPER_template = (rs, i) -> {
        CouponsTemplateEntity e = new CouponsTemplateEntity();
        e.setId(rs.getLong("id"));
        e.setCategory(rs.getString("category"));
        e.setClaimedCount(rs.getLong("claimed_count"));
        e.setOffAmount(rs.getBigDecimal("off_amount"));
        e.setMinAmount(rs.getBigDecimal("min_amount"));
        e.setStatus(rs.getInt("status"));
        e.setTitle(rs.getString("title"));
        e.setValidDays(rs.getLong("valid_days"));
        e.setTotalStock(rs.getLong("total_stock"));
        var ts = rs.getTimestamp("created_at");
        e.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        return e;
    };
    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<UserCouponsEntity> ENTITY_MAPPER_user = (rs, i) -> {
        UserCouponsEntity e = new UserCouponsEntity();
        e.setId(rs.getLong("id"));
        e.setUsed(rs.getInt("used"));
        e.setUserId(rs.getLong("user_id"));
        e.setTemplateId(rs.getLong("template_id"));
        e.setOrderNo(rs.getString("order_no"));
        e.setOffAmount(rs.getBigDecimal("off_amount"));
        e.setMinAmount(rs.getBigDecimal("min_amount"));
        var ts = rs.getTimestamp("created_at");
        var expiresAt = rs.getTimestamp("expires_at");
        e.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        e.setExpiresAt(ts == null ? null : expiresAt.toLocalDateTime());
        return e;
    };
    /*
    *获取模板优惠卷展示
    *
     */
    public List<CouponsTemplateEntity> listCoupons(){
        String sql = "SELECT * FROM coupon_templates";
        return jdbc.query(sql, ENTITY_MAPPER_template);
    }
    /*
    *展示我的优惠卷
    *
     */
    public List<UserCouponsEntity> mineCoupons(Long userId,int status){
        String sql = "SELECT * FROM user_coupons WHERE user_id = ?";
        return jdbc.query(sql,ENTITY_MAPPER_user,userId);
    }
}
