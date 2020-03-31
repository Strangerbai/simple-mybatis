package com.mybatis.core.config;

import lombok.Data;

@Data
public class Function {

    private String sqlType;

    private String functionName;

    private String resultType;

    private Object resultInstance;

    private String executeSql;

}
