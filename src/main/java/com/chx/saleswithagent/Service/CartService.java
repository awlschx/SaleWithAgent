package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.VO.CartItemVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CartService {
    List<CartItemVO> list(Long userId);
    Boolean addItem(Long userId,CartItemDTO cartItemDTO);

    Boolean updateItem(Long productId, @Valid CartItemUpdateDTO dto);

    Boolean deleteItem(Long productId);

    Boolean reorder(Long productId);

    Object count();
}
