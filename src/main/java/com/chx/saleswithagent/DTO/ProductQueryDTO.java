package com.chx.saleswithagent.DTO;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private int page = 1; //前端展示的是第几页
    private int size = 8; //每页多少条数据
    private String cat = null; //null = 全部分类
    private String q = null;//前端搜索栏输入的值
    private String sort = "def";//前端点分类了之后还有下一级是按什么字段排序
    /*注意点，数据库sql语句orderby是不能用？占位符传值的，只能用字符串拼接*/
    private String order = "asc";//从大到小还是从小到大
}
