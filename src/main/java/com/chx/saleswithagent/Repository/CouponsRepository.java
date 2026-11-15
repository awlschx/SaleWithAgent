package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.Common.BizException;
import com.chx.saleswithagent.Constant.ErrorCode;
import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
import com.chx.saleswithagent.Entity.UserCouponsEntity;
import com.chx.saleswithagent.VO.UserCouponsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        var ts = rs.getTimestamp("created_at");
        var expiresAt = rs.getTimestamp("expire_at");
        e.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        e.setExpiresAt(ts == null ? null : expiresAt.toLocalDateTime());
        return e;
    };

    /*
    *用户领取优惠卷
    *
     */
    @Transactional
    public UserCouponsVO clickCoupons(Long userId, Long templateId) {
        try {
            int rows = jdbc.update(
                    "INSERT INTO user_coupons (user_id, template_id, used, expire_at) " +
                            "SELECT ?, t.id, 0, DATE_ADD(NOW(), INTERVAL t.valid_days DAY) " +
                            "FROM coupon_templates t " +
                            "WHERE t.id = ? AND t.status = 1 AND t.claimed_count < t.total_stock",
                    userId, templateId);

            if (rows == 0) {
                throw new BizException(ErrorCode.COMMON_ERROR, "优惠券已领完或已下架");
            }

            jdbc.update(
                    "UPDATE coupon_templates SET claimed_count = claimed_count + 1 WHERE id = ?",
                    templateId);

            // 再查一次返回给前端
            return queryUserCoupon(userId, templateId);

        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.COMMON_ERROR, "已经领取过该优惠券");
        }
    }

    public UserCouponsVO queryUserCoupon(Long userId, Long templateId) {
        String sql =(
                "SELECT uc.id, uc.template_id, t.title, t.min_amount, t.off_amount, " +
                        "       t.category, uc.used, uc.expire_at " +
                        "FROM user_coupons uc " +
                        "JOIN coupon_templates t ON t.id = uc.template_id " +
                        "WHERE uc.user_id = ? AND template_id = ?");
        try {
            return jdbc.queryForObject(sql, (rs, i) -> {
                UserCouponsVO v = new UserCouponsVO();
                v.setId(rs.getLong("id"));
                v.setTemplateId(rs.getLong("template_id"));
                v.setTitle(rs.getString("title"));
                v.setMinAmount(rs.getBigDecimal("min_amount"));
                v.setOffAmount(rs.getBigDecimal("off_amount"));
                v.setCategory(rs.getString("category"));
                v.setUsed(rs.getBoolean("used"));
                v.setExpireAt(rs.getTimestamp("expire_at").toLocalDateTime());
                return v;
            },userId,templateId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
    public List<UserCouponsVO> mineCoupons(Long userId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT uc.id, uc.template_id, t.title, t.min_amount, t.off_amount, " +
                        "       t.category, uc.used, uc.expire_at " +
                        "FROM user_coupons uc " +
                        "JOIN coupon_templates t ON t.id = uc.template_id " +
                        "WHERE uc.user_id = ? ");
        if ("ok".equals(status)) {
            sql.append("AND uc.used = 0 AND uc.expire_at > NOW() ");
        } else if ("dead".equals(status)) {
            sql.append("AND (uc.used = 1 OR uc.expire_at <= NOW()) ");
        }
        sql.append("ORDER BY uc.expire_at");

        return jdbc.query(sql.toString(), (rs, i) -> {
            UserCouponsVO v = new UserCouponsVO();
            v.setId(rs.getLong("id"));
            v.setTemplateId(rs.getLong("template_id"));
            v.setTitle(rs.getString("title"));
            v.setMinAmount(rs.getBigDecimal("min_amount"));
            v.setOffAmount(rs.getBigDecimal("off_amount"));
            v.setCategory(rs.getString("category"));
            v.setUsed(rs.getBoolean("used"));
            v.setExpireAt(rs.getTimestamp("expire_at").toLocalDateTime());
            return v;
        }, userId);
    }
    /*
    *根据user_coupon的id查优惠卷
    *
     */
    public UserCouponsVO queryUserCouponById(Long userId, Long userCouponId) {
        String sql =
                "SELECT uc.id, uc.template_id, t.title, t.min_amount, t.off_amount, " +
                        "       t.category, uc.used, uc.expire_at " +
                        "FROM user_coupons uc " +
                        "JOIN coupon_templates t ON t.id = uc.template_id " +
                        "WHERE uc.id = ? AND uc.user_id = ?";
        List<UserCouponsVO> list = jdbc.query(sql, (rs, i) -> {
            UserCouponsVO v = new UserCouponsVO();
            v.setId(rs.getLong("id"));
            v.setTemplateId(rs.getLong("template_id"));
            v.setTitle(rs.getString("title"));
            v.setMinAmount(rs.getBigDecimal("min_amount"));
            v.setOffAmount(rs.getBigDecimal("off_amount"));
            v.setCategory(rs.getString("category"));
            v.setUsed(rs.getBoolean("used"));
            v.setExpireAt(rs.getTimestamp("expire_at").toLocalDateTime());
            return v;
        }, userCouponId, userId);
        return list.isEmpty() ? null : list.get(0);
    }
}
