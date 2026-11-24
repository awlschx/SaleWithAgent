package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.BannerService;
import com.chx.saleswithagent.VO.BannerVO;
import com.chx.saleswithagent.VO.BannersVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@RestController
@Slf4j
@RequestMapping("/api/banners")
public class BannerController {
    @Autowired
    private BannerService bannerService;
    /*
    *获取商品轮播
     */
    @GetMapping
    public Result<List<BannerVO>> getBanners(){
        return Result.ok(bannerService.getBanners());
    }
}
