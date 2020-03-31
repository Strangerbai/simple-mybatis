package com.mybatis.core.sqlSession;

import com.mybatis.core.model.Route;

import java.sql.Connection;
import java.util.Date;

public class MyExcutor  implements Excutor{

    private Connection connection = null;

    private MyExcutor(Connection connection){
        this.connection = connection;
    }

    public static class Factory{
        private static volatile MyExcutor myExcutor = null;

        public static MyExcutor getInstance(Connection connection){
            if(myExcutor!=null){
                return myExcutor;
            }
            synchronized (MyExcutor.class){
                if(myExcutor!=null){
                    return myExcutor;
                }
                return new MyExcutor(connection);
            }
        }
    }


    @Override
    public <T> T query(String sql, String params, String resultType) {
        Route route = new Route();
        route.setId(1);
        route.setFilters("1111");
        route.setGmtCreate(new Date());
        route.setGmtUpdate(new Date());
        return (T)route;
    }
}
