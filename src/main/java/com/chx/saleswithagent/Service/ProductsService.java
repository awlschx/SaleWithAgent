package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.DTO.ReviewQueryDTO;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.VO.ProductsVO;
import com.chx.saleswithagent.VO.ReviewVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductsService {
    List<ProductsVO> ListProducts();

    PageResult<ProductsVO> pageProduct(ProductQueryDTO productQueryDTO);

    PageResult<ProductsVO> flashProducts(ProductQueryDTO productQueryDTO);

    ProductsVO getProduct(long id);

    PageResult<ReviewVO> getProductReviews(long id, ReviewQueryDTO reviewQueryDTO);
}
