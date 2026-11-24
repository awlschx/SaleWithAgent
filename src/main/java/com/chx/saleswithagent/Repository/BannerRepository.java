package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.VO.BannerVO;
import com.chx.saleswithagent.VO.BannersVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class BannerRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedjdbc;
    public BannerRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.jdbc = jdbc;
        this.namedjdbc = namedParameterJdbcTemplate;
    }

    /*
    *获取商品轮播
     */
    public List<BannerVO> getBanners() {
       String sql = "select * from banners where (start_at IS NULL OR start_at <= :now)\n" +
               "and (end_at   IS NULL OR end_at   >  :now)";
        SqlParameterSource p = new MapSqlParameterSource("now", LocalDateTime.now());
       return namedjdbc.query(sql,p,(rs, i)->{
           BannerVO bannerVO = new BannerVO();
           bannerVO.setId(rs.getInt("id"));
           bannerVO.setTitle(rs.getString("title"));
           bannerVO.setDescription(rs.getString("description"));
           bannerVO.setBgColor(rs.getString("bg_color"));
           bannerVO.setButtonText(rs.getString("button_text"));
           bannerVO.setLinkType(rs.getString("link_type"));
           bannerVO.setLinkTarget(rs.getString("link_target"));
           return bannerVO;

       });

    }
}
