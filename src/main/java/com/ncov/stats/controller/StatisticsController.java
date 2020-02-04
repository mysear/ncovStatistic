package com.ncov.stats.controller;

import com.ncov.stats.request.GetNcovStatsReq;
import com.ncov.stats.request.SetNcovInfoReq;
import com.ncov.stats.result.CommonResult;
import com.ncov.stats.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author contact@vector.link
 */
@RestController
public class StatisticsController {
    @Autowired
    StatisticsService statisticsService;

    /**
     * 功能描述：获取ncov统计数据
     *
     * @param req
     * @param reqBody
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/stats/getNcovStats")
    @ResponseBody
    public CommonResult getNcovStats(HttpServletRequest req,
                                     @RequestBody GetNcovStatsReq reqBody) {
        CommonResult result = new CommonResult();

        Object resp = statisticsService.getNcovStats(reqBody);
        if (null != resp) {
            result.success(resp);
        } else {
            result.fail();
        }
        return result;
    }

    /**
     * 功能描述：设置ncov数据
     * @param req
     * @param reqBody
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/stats/setNcovInfo")
    @ResponseBody
    public CommonResult setNcovInfo(HttpServletRequest req,
                                    @RequestBody SetNcovInfoReq reqBody) {
        CommonResult result = new CommonResult();

        try {
            Object resp = statisticsService.setNcovInfo(reqBody);
            if (null != resp) {
                result.success(resp);
            } else {
                result.fail();
            }
        } catch (Exception e) {
            result.fail(e.getMessage());
        }
        return result;
    }

    /**
     * 功能描述：获取区域列表
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/stats/getRegionList")
    @ResponseBody
    public CommonResult getRegionList() {
        CommonResult result = new CommonResult();

        try {
            Object resp = statisticsService.getRegionList();
            if (null != resp) {
                result.success(resp);
            } else {
                result.fail();
            }
        } catch (Exception e) {
            result.fail(e.getMessage());
        }
        return result;
    }

}
