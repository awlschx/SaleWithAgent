package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.Repository.CartRepository;
import com.chx.saleswithagent.Service.CartService;
import com.chx.saleswithagent.VO.CartItemVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<CartItemVO> list(Long userId) {

        return cartRepository.list(userId);
    }

    @Override
    public Boolean addItem(Long userId,CartItemDTO cartItemDTO) {
       int res =  cartRepository.addOrMerge(userId, cartItemDTO.getProductId(), cartItemDTO.getSpec(), cartItemDTO.getQuantity(), cartItemDTO.getSelected());
        return  res > 0 ;
    }

    @Override
    public Boolean updateItem(Long productId, CartItemUpdateDTO dto) {
        return null;
    }

    @Override
    public Boolean deleteItem(Long productId) {
        return null;
    }

    @Override
    public Boolean reorder(Long productId) {
        return null;
    }

    @Override
    public Object count() {
        return null;
    }
}
