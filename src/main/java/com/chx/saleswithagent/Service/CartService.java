package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.AllSelectedDTO;
import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.VO.CartItemVO;
import jakarta.validation.Valid;

import java.util.List;

public interface CartService {
    List<CartItemVO> list(Long userId);
    Boolean addItem(Long userId,CartItemDTO cartItemDTO);

    Boolean updateItem(Long userId,Long itemId, @Valid CartItemUpdateDTO dto);

    Boolean deleteItem(Long userId,Long itemId);

    Boolean reorder(Long itemId);

    int count(Long itemId);

    Boolean allSelected(Long userId, AllSelectedDTO allSelectedDTO);

    Boolean deleteSelected(long id);
}
