package com.chx.saleswithagent.Service.Impl;


import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.Repository.ProductsRepository;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.Service.ProductsService;
import com.chx.saleswithagent.VO.ProductsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductsServiceImpl implements ProductsService {
    //注入repository
    private final ProductsRepository productsRepository;

    public ProductsServiceImpl(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public List<ProductsVO> ListProducts() {
        return productsRepository.ListProducts();
    }

    @Override
    public PageResult<ProductsVO> pageProduct(ProductQueryDTO productQueryDTO) {
        return productsRepository.pageProduct(productQueryDTO);
    }
}
