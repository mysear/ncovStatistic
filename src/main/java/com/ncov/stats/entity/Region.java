package com.ncov.stats.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 功能描述: 区域信息
 * @author : contact@vector.link
 */
@Data
@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 区域名称
     */
    @Column(name = "name", columnDefinition = "varchar(100) default '' null COMMENT '区域名称'")
    private String name;

    /**
     * 简称
     */
    @Column(name = "abbreviation", columnDefinition = "varchar(20) default '' COMMENT '简称'")
    private String abbreviation;

    /**
     * 代号
     */
    @Column(name = "code", columnDefinition = "varchar(20) default '' COMMENT '代号'")
    private String code;
}

