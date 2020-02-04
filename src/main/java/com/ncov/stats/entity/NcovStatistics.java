package com.ncov.stats.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 功能描述: 肺炎数量统计信息
 * @author : contact@vector.link
 */
@Data
@Entity
@Table(name = "ncov_stat")
public class NcovStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 区域ID
     */
    @Column(name = "region_id", columnDefinition = "bigint COMMENT '区域ID'")
    private long regionId;

    /**
     * 确诊增加数量
     */
    @Column(name = "diag_increase_quantity", columnDefinition = "bigint default -1 COMMENT '确诊增加数量'")
    private long diagIncreaseQuantity;

    /**
     * 确诊总量
     */
    @Column(name = "diag_total_quantity", columnDefinition = "bigint default -1 COMMENT '确诊总量'")
    private long diagTotalQuantity;

    /**
     * 疑似增加数量
     */
    @Column(name = "susp_increase_quantity", columnDefinition = "bigint default -1 COMMENT '疑似增加数量'")
    private long suspIncreaseQuantity;

    /**
     * 疑似总量
     */
    @Column(name = "susp_total_quantity", columnDefinition = "bigint default -1 COMMENT '疑似总量'")
    private long suspTotalQuantity;

    /**
     * 痊愈增加数量
     */
    @Column(name = "recv_increase_quantity", columnDefinition = "bigint default -1 COMMENT '痊愈增加数量'")
    private long recvIncreaseQuantity;

    /**
     * 痊愈总量
     */
    @Column(name = "recv_total_quantity", columnDefinition = "bigint default -1 COMMENT '痊愈总量'")
    private long recvTotalQuantity;

    /**
     * 死亡增加数量
     */
    @Column(name = "death_increase_quantity", columnDefinition = "bigint default -1 COMMENT '死亡增加数量'")
    private long deathIncreaseQuantity;

    /**
     * 死亡总量
     */
    @Column(name = "death_total_quantity", columnDefinition = "bigint default -1 COMMENT '死亡总量'")
    private long deathTotalQuantity;

    /**
     * 统计日期
     */
    @Column(name = "stat_date", columnDefinition = "bigint COMMENT '统计日期'")
    private long statDate;
}

