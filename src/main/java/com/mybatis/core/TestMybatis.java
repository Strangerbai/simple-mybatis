package com.mybatis.core;

import com.mybatis.core.config.ConfigurationBean;
import com.mybatis.core.mapper.RouteMapper;
import com.mybatis.core.model.Route;
import com.mybatis.core.sqlSession.MyConfiguration;
import com.alibaba.fastjson.JSON;
import com.mybatis.core.sqlSession.MySqlSession;

public class TestMybatis {


    public static void main(String[] args) {
        MyConfiguration myConfiguration = new MyConfiguration();
        ConfigurationBean configurationBean = myConfiguration.build("config.xml");
        MySqlSession mySqlSession = MySqlSession.Factory.getInstance(configurationBean);
        RouteMapper routeMapper = mySqlSession.getMapper(RouteMapper.class);
        Route route = routeMapper.getRouteById(1);
        System.out.println(JSON.toJSONString(route));
    }


}
