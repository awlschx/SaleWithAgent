package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.VO.AddressVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AddressService {
    AddressVO addAddress(AddressDTO addressDTO);

    List<AddressVO> getAddress(Long userId);

    AddressVO updateAddress(Long id, AddressDTO addressDTO);

    Boolean deleteAddressById(Long id);
}
