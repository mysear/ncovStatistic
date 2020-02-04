package com.ncov.stats.request;

import lombok.Data;

/**
 * 功能描述: 获取确诊统计数据请求
 * @author : contact@vector.link
 */
@Data
public class GetNcovStatsReq {
    /**
     * 区域ID
     */
    private String region_id;

    /**
     * 开始日期
     */
    private String start_date;

    /**
     * 结束日期
     */
    private String end_date;
}
