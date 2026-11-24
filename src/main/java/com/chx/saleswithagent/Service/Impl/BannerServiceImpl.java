package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Repository.BannerRepository;
import com.chx.saleswithagent.Service.BannerService;
import com.chx.saleswithagent.VO.BannerVO;
import com.chx.saleswithagent.VO.BannersVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerRepository bannerRepository;

    @Override
    public List<BannerVO> getBanners() {
        return bannerRepository.getBanners();
    }
}
