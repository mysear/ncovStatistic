package com.ncov.stats.utils.jpa;


import lombok.Data;

import javax.persistence.Transient;

/**
 * 定义一下操作类型
 * equal 等于
 * like  相似/模糊查询
 * ge    大于或者等于 greater or equal
 * le   小于或者等于  less or equal
 * between  之间
 * in       集合内
 * isNull   为null
 * isNotNull 不为Null
 * */

@Data
public class SearchParam {
    @Transient
    public final static String EQ="equal";
    @Transient
    public final static String LIKE="like";
    @Transient
    public final static String GE="ge";
    @Transient
    public final static String LE="le";
    @Transient
    public final static String BETWEEN="between";
    @Transient
    public final static String IN="in";
    @Transient
    public final static String ISNULL="isnull";
    @Transient
    public final static String NOTNULL="notnull";
    //参数名称
    private String name;
    //比较原则
    private String func;
    //值
    private Object value[];

    public SearchParam(){
        value=new Object[2];
    }

}
