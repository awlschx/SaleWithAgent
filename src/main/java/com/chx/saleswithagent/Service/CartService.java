package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.Common.Result;
import com.chx.saleswithagent.DTO.CartDTO;
import com.chx.saleswithagent.VO.CartVO;

import java.util.List;

public interface CartService {
    public List<CartVO> list();
    public Result addItem(CartDTO cartDTO);
}
