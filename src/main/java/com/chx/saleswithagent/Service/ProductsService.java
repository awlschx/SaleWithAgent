package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.VO.ProductsVO;

import java.util.List;

public interface ProductsService {
    List<ProductsVO> ListProducts();

    PageResult<ProductsVO> pageProduct(ProductQueryDTO productQueryDTO);
}
