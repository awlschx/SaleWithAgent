package com.chx.saleswithagent.Service;


import com.chx.saleswithagent.VO.ProductsVO;
import com.chx.saleswithagent.VO.ToggleFavoriteVO;

import java.util.List;

public interface FavoriteService {
    List<ProductsVO> getFavorite(Long userId);

    ToggleFavoriteVO toggleFavorite(Long userId, Long productId);
}
