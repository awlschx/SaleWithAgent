package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class CouponsRepository {
    private JdbcTemplate jdbc;
    public CouponsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<CouponsTemplateEntity> ENTITY_MAPPER = (rs, i) -> {
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
    /*
    *获取模板优惠卷展示
    *
     */
    public List<CouponsTemplateEntity> listCoupons(){
        String sql = "SELECT * FROM coupon_templates";
        return jdbc.query(sql, ENTITY_MAPPER);
    }

}
