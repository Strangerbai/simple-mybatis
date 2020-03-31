package com.mybatis.core.sqlSession;

import com.alibaba.fastjson.JSON;
import com.mybatis.core.config.ConfigurationBean;
import com.mybatis.core.config.Function;
import com.mybatis.core.config.MapperBean;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Data
public class MyMapperProxy implements InvocationHandler {

    private ConfigurationBean configurationBean;
    private MySqlSession mySqlSession;

    private MyConfiguration configuration = new MyConfiguration();

    public MyMapperProxy(ConfigurationBean myConfiguration, MySqlSession mySqlSession){
        this.configurationBean = myConfiguration;
        this.mySqlSession = mySqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String className = method.getDeclaringClass().getName();
        String mapperXmlName = "";
        for(String mapper : this.configurationBean.getMappers()){
            if(className.contains(mapper)){
                mapperXmlName = mapper + ".xml";
                break;
            }
        }
        MapperBean mapperBean = configuration.readMapper(mapperXmlName);
        if (!className.equals(mapperBean.getInterfaceName())){
            return null;
        }
        String params = String.valueOf(args[0]);
        List<Function> functionList = mapperBean.getFunctions();
        if(null!=functionList && 0!=functionList.size()){
            for(Function function : functionList){
                if(method.getName().equals(function.getFunctionName())){
                    return mySqlSession.selectOne(function.getExecuteSql(), params, function.getResultType());
                }
            }
        }
        return null;
    }
}
