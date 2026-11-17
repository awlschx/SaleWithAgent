package com.chx.saleswithagent.Service.Impl;


import com.chx.saleswithagent.Repository.FavouriteRepository;
import com.chx.saleswithagent.Service.FavoriteService;
import com.chx.saleswithagent.VO.ProductsVO;
import com.chx.saleswithagent.VO.ToggleFavoriteVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavouriteRepository favouriteRepository;
    @Override
    public List<ProductsVO> getFavorite(Long userId) {
        return favouriteRepository.getFavorite(userId);
    }

    @Override
    public ToggleFavoriteVO toggleFavorite(Long userId, Long productId) {
        log.info("toggleFavourite");
        ToggleFavoriteVO result = new ToggleFavoriteVO();
       result.setFavorite(favouriteRepository.toggleFavorite(userId,productId));
       return result;
    }
}
