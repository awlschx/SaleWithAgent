package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Repository.AddressRepository;
import com.chx.saleswithagent.Service.AddressService;
import com.chx.saleswithagent.VO.addressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public addressVO addAddress(AddressDTO addressDTO) {
            Long res = addressRepository.addAddress(addressDTO);
            if( res > 0){
                AddressEntity e = addressRepository.getAddressById(res);
                addressVO vo1 = new addressVO();
                vo1.setId(e.getId());
                vo1.setName(e.getName());
                vo1.setDetail(e.getDetail());
                vo1.setPhone(e.getPhone());
                vo1.setIsDefault(e.getIsDefault());
                vo1.setRegion(e.getRegion());
                return vo1;
            }
        return null;
    }

    @Override
    public List<addressVO> getAddress(Long userId) {
        List<addressVO> vo = new ArrayList<>();
        List<AddressEntity> e = new ArrayList<>();
        e = addressRepository.getAddressByUserId(userId);
        for (AddressEntity a:e){
            addressVO vo1 = new addressVO();
            vo1.setId(a.getId());
            vo1.setName(a.getName());
            vo1.setDetail(a.getDetail());
            vo1.setPhone(a.getPhone());
            vo1.setIsDefault(a.getIsDefault());
            vo1.setRegion(a.getRegion());
            vo.add(vo1);
        }
        return vo;
    }
}
