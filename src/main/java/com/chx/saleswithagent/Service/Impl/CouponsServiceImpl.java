package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
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
    public List<UserCouponsVO> mineCoupons(Long userId,String status) {
       return  couponsRepository.mineCoupons(userId,status);

    }

    @Override
    public UserCouponsVO clickCoupons(Long userId, Long templateId) {
        return couponsRepository.clickCoupons(userId,templateId);
    }
}
