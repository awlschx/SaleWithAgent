package com.chx.saleswithagent.VO;

import lombok.Data;

@Data
public class BannerVO {
    private Integer id;
    private String title;
    private String description;
    private String buttonText;
    private String linkType;
    private String linkTarget;
    private String image;
}
