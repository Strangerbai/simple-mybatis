package com.mybatis.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class Route {
    private Integer id;
    private String routeId;
    private String filters;
    private String uri;
    private Integer order;
    private Date gmtCreate;
    private Date gmtUpdate;
    private String user;
}
