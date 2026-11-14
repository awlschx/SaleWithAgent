package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.Entity.AddressEntity;
import com.chx.saleswithagent.Repository.AddressRepository;
import com.chx.saleswithagent.Service.AddressService;
import com.chx.saleswithagent.VO.AddressVO;
import lombok.extern.slf4j.Slf4j;
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
    public AddressVO addAddress(AddressDTO addressDTO) {
            Long res = addressRepository.addAddress(addressDTO);
            if( res > 0){
                AddressEntity e = addressRepository.getAddressById(res);
                AddressVO vo1 = new AddressVO();
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
    public List<AddressVO> getAddress(Long userId) {
        List<AddressVO> vo = new ArrayList<>();
        List<AddressEntity> e = new ArrayList<>();
        e = addressRepository.getAddressByUserId(userId);
        for (AddressEntity a:e){
            AddressVO vo1 = new AddressVO();
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

    @Override
    public AddressVO updateAddress(Long id, AddressDTO addressDTO) {
        AddressEntity e = addressRepository.updateAddress(id, addressDTO);
        AddressVO v = new AddressVO();
        v.setId(e.getId());
        v.setName(e.getName());
        v.setDetail(e.getDetail());
        v.setPhone(e.getPhone());
        v.setIsDefault(e.getIsDefault());
        v.setRegion(e.getRegion());
        return v;
    }

    @Override
    public Boolean deleteAddressById(Long id) {
        return addressRepository.deleteAddressById(id) > 0;
    }
}
