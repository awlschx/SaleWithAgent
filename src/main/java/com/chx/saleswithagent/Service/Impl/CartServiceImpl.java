package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Common.Result;
import com.chx.saleswithagent.DTO.CartDTO;
import com.chx.saleswithagent.Service.CartService;
import com.chx.saleswithagent.VO.CartVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Override
    public List<CartVO> list() {
        return List.of();
    }

    @Override
    public Result addItem(CartDTO cartDTO) {
        return null;
    }
}
