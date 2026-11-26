package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.VO.ProductsVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductsService {
    List<ProductsVO> ListProducts();

    PageResult<ProductsVO> pageProduct(ProductQueryDTO productQueryDTO);

    PageResult<ProductsVO> flashProducts(ProductQueryDTO productQueryDTO);

    ProductsVO getProduct(long id);

    List<Map<String, Object>> getCategories();

    List<String> getProductSpecs(long id);
}
