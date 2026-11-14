package com.chx.saleswithagent.Service.Impl;

import ch.qos.logback.core.spi.ComponentTracker;
import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
import com.chx.saleswithagent.Entity.UserCouponsEntity;
import com.chx.saleswithagent.Repository.CouponsRepository;
import com.chx.saleswithagent.Service.CouponsService;
import com.chx.saleswithagent.VO.CouponsTemplateVO;
import com.chx.saleswithagent.VO.UserCouponsVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponsServiceImpl implements CouponsService {
    @Resource
    private CouponsRepository couponsRepository;
    @Override
    public List<CouponsTemplateVO> listCoupons() {
        List<CouponsTemplateEntity> e = couponsRepository.listCoupons();
        List<CouponsTemplateVO> list = new ArrayList<>();
        for (CouponsTemplateEntity couponsTemplateEntity : e) {
            CouponsTemplateVO couponsTemplateVO = new CouponsTemplateVO();
            BeanUtils.copyProperties(couponsTemplateEntity, couponsTemplateVO);
            list.add(couponsTemplateVO);
        }
        return list;
    }

    @Override
    public List<UserCouponsVO> mineCoupons(Long userId,int status) {
        List<UserCouponsEntity> e = couponsRepository.mineCoupons(userId,status);
        List<UserCouponsVO> list = new ArrayList<>();
        for (UserCouponsEntity c : e) {
            UserCouponsVO v = new UserCouponsVO();
            BeanUtils.copyProperties(c, UserCouponsVO.class);
            list.add(v);
        }
        return list;
    }
}
