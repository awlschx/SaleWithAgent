package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Entity.CouponsTemplateEntity;
import com.chx.saleswithagent.Repository.CouponsRepository;
import com.chx.saleswithagent.Service.CouponsService;
import com.chx.saleswithagent.VO.CouponsTemplateVO;
import com.chx.saleswithagent.VO.UserCouponsVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponsServiceImpl implements CouponsService {
    @Resource
    private CouponsRepository couponsRepository;
    @Override
    @Transactional
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
    @Transactional
    public List<UserCouponsVO> mineCoupons(Long userId,String status) {
       return  couponsRepository.mineCoupons(userId,status);

    }

    @Override
    @Transactional
    public UserCouponsVO clickCoupons(Long userId, Long templateId) {
        return couponsRepository.clickCoupons(userId,templateId);
    }

    @Override
    @Transactional
    public Boolean deleteCoupons(Long userCouponsId) {
        return couponsRepository.deleteCoupons(userCouponsId);
    }
}
