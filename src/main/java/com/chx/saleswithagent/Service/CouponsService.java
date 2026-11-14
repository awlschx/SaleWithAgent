package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.VO.CouponsTemplateVO;
import com.chx.saleswithagent.VO.UserCouponsVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CouponsService {
    List<CouponsTemplateVO> listCoupons();

    List<UserCouponsVO> mineCoupons(Long userId ,int status);
}
