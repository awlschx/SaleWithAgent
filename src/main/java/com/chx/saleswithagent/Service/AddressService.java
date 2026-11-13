package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.VO.addressVO;

import java.util.List;

public interface AddressService {
    addressVO addAddress(AddressDTO addressDTO);

    List<addressVO> getAddress(Long userId);
}
