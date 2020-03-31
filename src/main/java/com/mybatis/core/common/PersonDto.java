package com.mybatis.core.common;

import lombok.Data;

@RelMapper
@Data
public class PersonDto {

    @RelMapper(value = "perId")
    private Integer personId;

    @RelMapper(value = "perName")
    private String personName;

    private String email;

    private String perPhone;

    private String extension;

    private Integer level;
}
