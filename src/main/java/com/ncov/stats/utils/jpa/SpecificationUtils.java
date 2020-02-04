package com.ncov.stats.utils.jpa;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Date;

public final class SpecificationUtils {
    /**
     *  * equal     等于
     *  * notEqual  不等于
     *  * like      相似/模糊查询
     *  * ge        大于或者等于 greater or equal
     *  * le        小于或者等于  less or equal
     *  * between   之间
     *  * in        集合内
     *  * isNull    为null
     *  * isNotNull 不为Null
     * */




    public static Specification equal(String attribute, Object value) {
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }

    public static Specification notEqual(String attribute, Object value) {
        return (root, query, cb) -> cb.notEqual(root.get(attribute), value);
    }

    //大于或者等于
    public static Specification ge(String attribute, Number value) {
        return (root, query, cb) -> cb.ge(root.get(attribute), value);
    }

    //小于或者等于
    public static Specification le(String attribute, Number value) {
        return (root, query, cb) -> cb.le(root.get(attribute), value);
    }


    //大于或者等于
    public static Specification ge(String attribute, Date value) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value);
    }

    //大于或者等于
    public static Specification ge(String attribute, String value) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value);
    }


    //小于或者等于
    public static Specification le(String attribute, Date value) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value);
    }

    //小于或者等于
    public static Specification le(String attribute, String value) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value);
    }

    public static Specification like(String attribute, String value) {
        return (root, query, cb) -> cb.like(root.get(attribute), "%" + value + "%");
    }

    public static Specification between(String attribute, Comparable min, Comparable max) {
        return (root, query, cb) -> cb.between(root.get(attribute), min, max);
    }

    public static Specification in(String attribute, Collection c) {
        return (root, query, cb) -> root.get(attribute).in(c);
    }

    public static Specification isNull(String attribute) {
        return (root, query, cb) -> cb.isNull(root.get(attribute));
    }

    public static Specification isNotNull(String attribute) {
        return (root, query, cb) -> cb.isNotNull(root.get(attribute));
    }


}
