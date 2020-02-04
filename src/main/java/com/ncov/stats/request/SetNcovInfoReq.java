package com.ncov.stats.request;

import lombok.Data;

/**
 * 功能描述: 获取确诊统计数据请求
 * @author : contact@vector.link
 */
@Data
public class SetNcovInfoReq {
    /**
     * 区域ID
     */
    private String region_id;

    /**
     * 确诊增加数量
     */
    private String diag_increase_quantity;

    /**
     * 确诊总量
     */
    private String diag_total_quantity;

    /**
     * 疑似增加数量
     */
    private String susp_increase_quantity;

    /**
     * 疑似总量
     */
    private String susp_total_quantity;

    /**
     * 痊愈增加数量
     */
    private String recv_increase_quantity;

    /**
     * 痊愈总量
     */
    private String recv_total_quantity;

    /**
     * 死亡增加数量
     */
    private String death_increase_quantity;

    /**
     * 死亡总量
     */
    private String death_total_quantity;

    /**
     * 统计日期
     */
    private String stat_date;
}
