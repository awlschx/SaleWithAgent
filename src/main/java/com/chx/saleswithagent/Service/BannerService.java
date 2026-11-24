package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.VO.BannerVO;
import com.chx.saleswithagent.VO.BannersVO;

import java.util.List;

public interface BannerService {
    List<BannerVO> getBanners();
}
