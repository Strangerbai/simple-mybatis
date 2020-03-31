package com.mybatis.core.sqlSession;

import com.mybatis.core.config.ConfigurationBean;
import com.mybatis.core.config.Function;
import com.mybatis.core.config.MapperBean;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyConfiguration {

    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    public ConfigurationBean build(String resource){
        try{
            InputStream stream = loader.getResourceAsStream(resource);
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            return parseNode(root);
        } catch (Exception e){
            throw new RuntimeException("error occurred in parse xml" + resource);
        }
    }

    private ConfigurationBean parseNode(Element node) throws ClassNotFoundException {
        if(!node.getName().equals("database")){
            throw new RuntimeException("root should be <database>");
        }
        ConfigurationBean configurationBean = new ConfigurationBean();
        List<String> mappers = new ArrayList<>();
        String driverClassName = null;
        String url = null;
        String username = null;
        String password = null;
        String mapperNames = null;
        for(Object item : node.elements("property")){
            Element i = (Element) item;
            String value = getValue(i);
            String name = i.attributeValue("name");
            if(name == null || value == null){
                throw new RuntimeException("<database> should contain name and value");
            }
            switch (name) {
                case "url" : url = value; break;
                case "username" : username = value; break;
                case "password" : password = value; break;
                case "driverClassName" : driverClassName = value; break;
                case "mapperNames" : mapperNames = value;break;
                default: throw new RuntimeException("<database> <property> contain unknow name");
            }
        }
        Class.forName(driverClassName);
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] mappersList = mapperNames.trim().split(",");
        configurationBean.setConnection(connection);
        configurationBean.setMappers(mappersList);
        return configurationBean;
    }

    private String getValue(Element node){
        return node.hasContent() ? node.getText() : node.attributeValue("value");
    }


    public MapperBean readMapper(String resource){
        try{
            InputStream stream = loader.getResourceAsStream(resource);
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            return parseMapperNode(root);
        } catch (Exception e){
            throw new RuntimeException("error occurred in parse xml" + resource);
        }
    }

    public MapperBean parseMapperNode(Element node){
        MapperBean mapperBean = new MapperBean();
        mapperBean.setInterfaceName(node.attributeValue("namespace").trim());
        List<Function> functions = new ArrayList<>();
        Iterator rootIter = node.elementIterator();
        while(rootIter.hasNext()){
            Function function = new Function();
            Element e = (Element) rootIter.next();
            String sqlType = e.getName().trim();
            String functionName = e.attributeValue("id").trim();
            String sql = e.getText().trim();
            String resultType = e.attributeValue("resultType").trim();
            function.setExecuteSql(sql);
            function.setFunctionName(functionName);
            function.setSqlType(sqlType);
            function.setResultType(resultType);
            Object newInstance = null;
            try{
                newInstance = Class.forName(resultType);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            function.setResultInstance(newInstance);
            functions.add(function);
        }
        mapperBean.setFunctions(functions);
        return mapperBean;
    }


}
