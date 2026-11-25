package com.chx.saleswithagent.DTO;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long productId;
    private int rating;
    private String spec;
    private String content;
}
