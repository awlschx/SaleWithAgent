package com.chx.saleswithagent.Repository;

import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Entity.CartItemEntity;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AddressRepository {
    private final JdbcTemplate jdbc;

    public AddressRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    /* Entity 的 RowMapper：查询整行、做业务判断时用 */
    private static final RowMapper<AddressEntity> ENTITY_MAPPER = (rs, i) -> {
        AddressEntity e = new AddressEntity();
        e.setId(rs.getLong("id"));
        e.setUserId(rs.getLong("user_id"));
        e.setDetail(rs.getString("detail"));
        e.setIsDefault(rs.getBoolean("is_default"));
        e.setName(rs.getString("name"));
        e.setPhone(rs.getString("phone"));
        e.setRegion(rs.getString("region"));
        var ts = rs.getTimestamp("created_at");
        e.setCreateTime(ts == null ? null : ts.toLocalDateTime());
        return e;
    };

    /*
    *新增地址
    *
     */
    public Long addAddress(AddressDTO dto) {
        String sql = "INSERT INTO addresses (user_id,name,phone,region,detail,is_default) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, dto.getUserId());
            ps.setString(2, dto.getName());
            ps.setString(3, dto.getPhone());
            ps.setString(4, dto.getRegion());
            ps.setString(5, dto.getDetail());
            ps.setInt(6, Boolean.TRUE.equals(dto.getIsDefault()) ? 1 : 0);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();

    }
    /*
    *根据用户id获取地址
    *
     */
    public List<AddressEntity> getAddressByUserId(Long userId){
        String sql = "SELECT * FROM addresses WHERE user_id=? ORDER BY is_default DESC ,id DESC";
        return jdbc.query(sql,ENTITY_MAPPER,userId);
    }
    /*
    *根据id获取地址
    *
    *
     */
    public AddressEntity getAddressById(Long id){
        String sql = "SELECT * FROM addresses WHERE id = ?";
        return jdbc.queryForObject(sql,ENTITY_MAPPER,id);
    }
}
