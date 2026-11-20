package com.chx.saleswithagent.Controller;


import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.FavoriteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/favorites")
public class FavoriteController {
    Long userId = UserContext.getUserId();
    @Resource
    private FavoriteService favoriteService;
    /*
    *获取收藏列表
    *
     */
    @GetMapping
    public Result getFavourite(){
        return Result.ok(favoriteService.getFavorite(userId));
    }

    /*
    *收藏商品
    *
     */
    @PostMapping("/{productId}/toggle")
    public Result toggleFavorite(@PathVariable Long productId){
        return Result.ok(favoriteService.toggleFavorite(userId,productId));
    }
}
