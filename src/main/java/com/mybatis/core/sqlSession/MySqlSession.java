package com.mybatis.core.sqlSession;

import com.mybatis.core.config.ConfigurationBean;

import java.lang.reflect.Proxy;
import java.util.Map;



public class MySqlSession {

    private ConfigurationBean configurationBean;

    private MySqlSession(ConfigurationBean configurationBean){
        this.configurationBean = configurationBean;
    }

    public static class Factory{
        private static volatile MySqlSession mySqlSession = null;

        public static MySqlSession getInstance(ConfigurationBean configurationBean){
            if(mySqlSession!=null){
                return mySqlSession;
            }
            synchronized (MySqlSession.class){
                if(mySqlSession!=null){
                    return mySqlSession;
                }
                return new MySqlSession(configurationBean);

            }
        }
    }

    public <T> T selectOne(String statement,String params, String resultType){
        Excutor excutor = MyExcutor.Factory.getInstance(this.configurationBean.getConnection());
        return excutor.query(statement, params,resultType);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> cls){
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new MyMapperProxy(configurationBean, this));
    }



}
