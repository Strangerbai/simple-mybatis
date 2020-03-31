package com.mybatis.core.sqlSession;

import java.util.Map;

public interface Excutor {

    public <T> T query(String sql, String params, String resultType);
}
