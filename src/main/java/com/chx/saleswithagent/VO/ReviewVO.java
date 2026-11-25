package com.chx.saleswithagent.VO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {
    private Long id;
    private int productId;
    private String userName;
    private String avatar;
    private int rating;//星级
    private String content;
    private String spec;
    private Integer usefulCount;
    private LocalDateTime updateTime;
    private List<String> images;
    private Integer useful;
}
