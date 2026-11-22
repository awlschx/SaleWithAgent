package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.Entity.CartItemEntity;
import com.chx.saleswithagent.Entity.ProductsEntity;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.VO.CartItemVO;
import com.chx.saleswithagent.VO.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
public class ProductsRepository {
    //注入jdbc
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    public ProductsRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbc = jdbc;
        this.namedJdbc = namedParameterJdbcTemplate;
    }
    /*
    *和数据库表做映射
    *
     */
    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<ProductsEntity> ENTITY_MAPPER = (rs, i) -> {
        ProductsEntity e = new ProductsEntity();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setPrice(rs.getFloat("price"));
        e.setCategory(rs.getString("category"));
        e.setOriginalPrice(rs.getFloat("original_price"));
        e.setSales(rs.getInt("sales"));
        e.setStock(rs.getInt("stock"));
        e.setHue(rs.getInt("hue"));
        e.setFlashPrice(rs.getFloat("flash_price"));
        e.setFlashPercent(rs.getInt("flash_percent"));
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
                    v.setId(rs.getLong("id"));
                    v.setName(rs.getString("name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setCategory(rs.getString("category"));
                    v.setOriginalPrice(rs.getBigDecimal("original_price"));
                    v.setSales(rs.getInt("sales"));
                    v.setStock(rs.getInt("stock"));
                    v.setHue(rs.getInt("hue"));
                    v.setFlashPrice(rs.getBigDecimal("flash_price"));
                    v.setFlashPercent(rs.getInt("flash_percent"));
                    var ts1 = rs.getTimestamp("created_at");
                    var ts2 = rs.getTimestamp("updated_at");
                    v.setCreateTime(ts1 == null ? null : ts1.toLocalDateTime());
                    v.setUpdateTime(ts2 == null ? null : ts2.toLocalDateTime());
                    return v;
                }
                );
    }

    public PageResult<ProductsVO> pageProduct(ProductQueryDTO productQueryDTO) {
        MapSqlParameterSource p = new  MapSqlParameterSource();
        //查总数和查具体共用一个条件体
        //先查一下有多少条，前端要展示“总共N条”
        StringBuilder countSql = new StringBuilder("select count(*) from products where 1=1");

        //productvo查询
        StringBuilder selectSql = new StringBuilder("select * from products where 1=1");
        //通用条件体的构建
        StringBuilder commonSql = new StringBuilder();
        //判断cat，是否按分类
        if (productQueryDTO.getCat() != null && !productQueryDTO.getCat().isBlank()) {
            commonSql.append(" and category =:cat ");
            p.addValue("cat",productQueryDTO.getCat().trim());
        }
        //判断q是不是非空，是不是在搜索栏搜索了
        if (productQueryDTO.getQ() != null && !productQueryDTO.getQ().isBlank()) {
            commonSql.append(" and (name like :kw or category like :kw )");
            p.addValue("kw","%"+escapeLike(productQueryDTO.getQ())+"%");
        }
        countSql.append(commonSql);
        String col = resolveColumn(productQueryDTO.getSort());
        String order = resolveOrder(productQueryDTO.getOrder());
        String orderBy = "id".equals(col)?col+" "+order:col+" "+order+",id asc";
        commonSql.append(" order by ").append(orderBy);
        //算出要跳过多少条
        int offset = (productQueryDTO.getPage()-1)*productQueryDTO.getSize();
        commonSql.append(" limit :size ").append(" offset :offset ");
        p.addValue("size", productQueryDTO.getSize()).addValue("offset", offset);
        //拼接

        selectSql.append(commonSql);
        long total = Objects.requireNonNullElse(
                namedJdbc.queryForObject(countSql.toString(),p,Long.class),0L);
        List<ProductsVO> selectQuery = namedJdbc.query(selectSql.toString(), p,
                (rs, i) -> {
                    ProductsVO v = new ProductsVO();
                    v.setId(rs.getLong("id"));
                    v.setName(rs.getString("name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setCategory(rs.getString("category"));
                    v.setOriginalPrice(rs.getBigDecimal("original_price"));
                    v.setSales(rs.getInt("sales"));
                    v.setStock(rs.getInt("stock"));
                    v.setHue(rs.getInt("hue"));
                    v.setFlashPrice(rs.getBigDecimal("flash_price"));
                    v.setFlashPercent(rs.getInt("flash_percent"));
                    var ts1 = rs.getTimestamp("created_at");
                    var ts2 = rs.getTimestamp("updated_at");
                    v.setCreateTime(ts1 == null ? null : ts1.toLocalDateTime());
                    v.setUpdateTime(ts2 == null ? null : ts2.toLocalDateTime());
                    return v;
                });
        log.info("selectQuery:{}",selectQuery);

        return PageResult.<ProductsVO>of(selectQuery,total,productQueryDTO.getPage(),productQueryDTO.getSize());

    }
    /*
    *orderby的白名单设置
    *
     */
    private static final Map<String,String> SORT_COLUMN = Map.of(
            "def","id",
            "sales","sales",
            "price","price"
    );
    private static final String DEFAULT_COLUMN = "id";
    //解决映射字段排序
    private static String resolveColumn(String sort){
        if(sort == null || sort.isEmpty()){
            return DEFAULT_COLUMN;
        }
        return SORT_COLUMN.getOrDefault(sort.trim().toLowerCase(Locale.ROOT), DEFAULT_COLUMN);//locale.root解决土耳其语的问题

    }
    //解决映射升降序
    private static String resolveOrder(String order){
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
    /*
    *字符转义
     */
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
