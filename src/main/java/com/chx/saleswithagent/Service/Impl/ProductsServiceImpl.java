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
import java.util.Map;

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

    @Override
    public PageResult<ProductsVO> flashProducts(ProductQueryDTO productQueryDTO) {
        return productsRepository.flashProducts(productQueryDTO);
    }

    @Override
    public ProductsVO getProduct(long id) {
        return productsRepository.getProduct(id);
    }

    @Override
    public List<Map<String, Object>> getCategories() {
        return productsRepository.getCategories();
    }

    @Override
    public List<String> getProductSpecs(long id) {
        return productsRepository.getProductSpecs(id);
    }
}
