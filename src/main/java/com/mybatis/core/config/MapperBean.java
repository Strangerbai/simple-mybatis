package com.mybatis.core.config;

import lombok.Data;

import java.util.List;

@Data
public class MapperBean {

   private String interfaceName;
   private List<Function> functions;
}
