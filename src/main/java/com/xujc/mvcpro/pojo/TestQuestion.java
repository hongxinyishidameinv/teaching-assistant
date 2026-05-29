package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestion {
    private Long id;
    private Long testId;
    private Long questionId;
    private BigDecimal score;
    
    private Question question;
}
