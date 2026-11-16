package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.AllSelectedDTO;
import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.Repository.CartRepository;
import com.chx.saleswithagent.Repository.OrdersRepository;
import com.chx.saleswithagent.Service.CartService;
import com.chx.saleswithagent.VO.CartItemVO;
import com.chx.saleswithagent.VO.CartVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public CartVO reorder(String orderNo,Long userId) {
        //后端按orderNo和userId查orderitems里面的商品，写入cartitems，返回给前端cartVO
        cartRepository.reorderOrders(orderNo,userId);
        return buildCart(userId);
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

    /* 统一构建购物车返回：条目 + 总件数 + 选中金额 */
    public CartVO buildCart(Long userId) {
        List<CartItemVO> items = list(userId);
        CartVO vo = new CartVO();
        vo.setItems(items);
        vo.setTotalQuantity(items.stream()
                .mapToInt(CartItemVO::getQuantity).sum());
        vo.setSelectedAmount(items.stream()
                .filter(CartItemVO::isSelected)
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }
}
