package com.mybatis.core.config;

import lombok.Data;

import java.sql.Connection;
import java.util.List;

@Data
public class ConfigurationBean {

    private Connection connection;
    private String[] mappers;
}
