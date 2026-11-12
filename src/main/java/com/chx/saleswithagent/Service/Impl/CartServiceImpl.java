package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.AllSelectedDTO;
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
    public Boolean updateItem(Long userId,Long itemId, CartItemUpdateDTO dto) {
        int res = cartRepository.updateItem(userId, itemId, dto);
        return res > 0;
    }

    @Override
    public Boolean deleteItem(Long userId,Long itemId) {
        int res = cartRepository.deleteItem(userId, itemId);
        return res > 0;
    }

    @Override
    public Boolean reorder(Long itemId) {
        return null;
    }

    @Override
    public int  count(Long userId) {

        return cartRepository.count(userId);
    }

    @Override
    public Boolean allSelected(Long userId, AllSelectedDTO allSelectedDTO) {
        return cartRepository.allSelected(userId,allSelectedDTO)>0;    }

    @Override
    public Boolean deleteSelected(long id) {
        return cartRepository.deleteSelected(id)>0;
    }
}
