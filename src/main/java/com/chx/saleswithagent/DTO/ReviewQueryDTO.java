package com.chx.saleswithagent.DTO;

import lombok.Data;

@Data
public class ReviewQueryDTO {
    private Integer page;
    private Integer size;
    private Integer rating;
}
