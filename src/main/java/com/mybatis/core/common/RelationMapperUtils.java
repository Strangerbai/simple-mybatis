package com.mybatis.core.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.springframework.util.ReflectionUtils;

public class RelationMapperUtils {

    public static Object entryAndDtoMapper(Object entry, Object dto) throws InvocationTargetException, IllegalAccessException{
        return enAndDtoMapper(entry, dto, true);
    }

    public static Object entryAndDtoMapper(Object entry, Object dto, boolean enToDto) throws InvocationTargetException, IllegalAccessException {
        return enAndDtoMapper(entry, dto, enToDto);
    }


    public static Object enAndDtoMapper(Object entry, Object dto, boolean enToDto) throws InvocationTargetException, IllegalAccessException {
        if(enToDto ? entry == null : dto == null){
            return null;
        }

        Class<?> entryClass = entry.getClass();
        Class<?> dtoClazz = dto.getClass();

        // 副本
        Class<?> entryClassDuplicate = entryClass;
        Class<?> dtoClazzDuplicate = dtoClazz;

        boolean dtoExistAnno = dtoClazz.isAnnotationPresent(RelMapper.class);

        List<Field> entryFieldList = new ArrayList<>() ;
        List<Field> dtoFieldList = new ArrayList<>() ;
        Map<Method[],Class<?>> map = new HashMap<>();
        Map<Method[],List<Field>> mapFiled = new HashMap<>();

        while (entryClassDuplicate != null || dtoClazzDuplicate !=null ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            if(entryClassDuplicate!=null){
                entryFieldList.addAll(Arrays.asList(entryClassDuplicate.getDeclaredFields()));
                entryClassDuplicate = entryClassDuplicate.getSuperclass();
            }else {
                dtoFieldList.addAll(Arrays.asList(dtoClazzDuplicate.getDeclaredFields()));
                dtoClazzDuplicate = dtoClazzDuplicate.getSuperclass(); //得到父类,然后赋给自己
            }
        }

        Method[] entrys = ReflectionUtils.getAllDeclaredMethods(entryClass);    //entity methods
        Method[] dtos = ReflectionUtils.getAllDeclaredMethods(dtoClazz);     //dto methods

        map.put(dtos, dtoClazz);
        map.put(entrys, entryClass);
        mapFiled.put(dtos, dtoFieldList);
        mapFiled.put(entrys, entryFieldList);

        String mName,fieldName,targetFieldType=null,fromFieldType=null,targetMapName = null,fromFieldName =null;Object value = null;
        String fromMapName=null;
        Method[] targetMethod = enToDto ? dtos : entrys;
        Method[] fromMethod = enToDto ? entrys : dtos;

        for(Method m : targetMethod) {
            if((mName=m.getName()).startsWith("set")) {    //只进set方法
                fieldName = mName.toLowerCase().charAt(3) + mName.substring(4);
                Field fdTarget = ReflectionUtils.findField(map.get(targetMethod), fieldName);
                fdTarget.setAccessible(true);
                if(fdTarget.isAnnotationPresent(RelMapper.class)||dtoExistAnno){
                    targetMapName = fdTarget.isAnnotationPresent(RelMapper.class) ? (fdTarget.getAnnotation(RelMapper.class).value().equals("")?fdTarget.getName():fdTarget.getAnnotation(RelMapper.class).value()):fdTarget.getName();
                    targetFieldType = fdTarget.getGenericType().toString().substring(fdTarget.getGenericType().toString().lastIndexOf(".") + 1);
                    for(Field fe : mapFiled.get(fromMethod)){
                        fe.setAccessible(true);
                        fromMapName = fe.isAnnotationPresent(RelMapper.class) ? (fe.getAnnotation(RelMapper.class).value().equals("")?fe.getName():fe.getAnnotation(RelMapper.class).value()):fe.getName();
                        if(fromMapName.equals(targetMapName) ){
                            fromFieldType = fe.getGenericType().toString().substring(fe.getGenericType().toString().lastIndexOf(".") + 1);
                            fromMapName = fe.getName();
                            break;
                        } else {
                            fromMapName = null;
                        }
                    }
                }
                if(targetMapName!= null && fromMapName!=null && fromFieldType.equals(targetFieldType)) {
                    for(Method md : fromMethod) {
                        String methodName = md.getName();
                        if(methodName.startsWith("get") && !methodName.toUpperCase().equals("GET"+targetMapName.toUpperCase())){
                           if(methodName.toUpperCase().equals("GET"+fromMapName.toUpperCase())){
                               if(md.invoke(enToDto ? entry : dto) == null) { break;}
                               value = md.invoke(enToDto ? entry : dto);
                               m.invoke(enToDto ? dto : entry, value);
                               break;
                           }
                        }else if(methodName.toUpperCase().equals("GET"+targetMapName.toUpperCase())){
                            if(md.invoke(enToDto ? entry : dto) == null) { break;}
                            value = md.invoke(enToDto ? entry : dto);
                            m.invoke(enToDto ? dto : entry, value);
                            break;
                        }
                    }

                }
            }
        }
        return enToDto ? dto : entry;
    }


    public static void main(String[] args) {
        try {
            Person person = new Person();
            person.setEmail("123@ddd.com");
            person.setPerAge("15");
            person.setPerId(1);
            person.setPerName("test");
            person.setPerPhone("12345678");
            person.setAddr("22222");
//            person.setLevel(44);
            PersonDto personDto =(PersonDto) RelationMapperUtils.entryAndDtoMapper(person,new PersonDto());
            //Dto数据转成Entity数据
            System.out.println(JSON.toJSONString(personDto));
            personDto.setPerPhone("87654321");
            Object person1 = RelationMapperUtils.entryAndDtoMapper(new Person(),personDto,false);
            System.out.println(JSON.toJSONString(person1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
