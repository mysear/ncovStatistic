package com.ncov.stats.service;

import com.ncov.stats.entity.NcovStatistics;
import com.ncov.stats.entity.Region;
import com.ncov.stats.repository.NcovStatisticsRepository;
import com.ncov.stats.repository.RegionRepository;
import com.ncov.stats.request.GetNcovStatsReq;
import com.ncov.stats.request.SetNcovInfoReq;
import com.ncov.stats.result.GetNcovStatsResult;
import com.ncov.stats.result.SetNcovInfoResult;
import com.ncov.stats.utils.StringUtils;
import com.ncov.stats.utils.jpa.SpecificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author contact@vector.link
 */
@Service
public class StatisticsService {
    @Autowired
    NcovStatisticsRepository ncovStatisticsRepository;

    @Autowired
    RegionRepository regionRepository;

    /**
     * 功能描述：获取ncov数据
     *
     * @param reqBody
     * @return
     */
    public Object getNcovStats(GetNcovStatsReq reqBody) {
        List<GetNcovStatsResult> diagnosisStatsResultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "statDate");
        Specification<NcovStatistics> specification = Specification.where(SpecificationUtils.ge("id", 0));

        // 区域ID
        if (StringUtils.isNotBlank(reqBody.getRegion_id())) {
            specification = specification.and(SpecificationUtils.equal("regionId", reqBody.getRegion_id()));
        }

        // 起止日期
        if (StringUtils.isNotBlank(reqBody.getStart_date()) && StringUtils.isNotBlank(reqBody.getEnd_date())) {
            specification = specification.and(SpecificationUtils.between("statDate",
                    Long.valueOf(reqBody.getStart_date()),
                    Long.valueOf(reqBody.getEnd_date())));
        } else if (StringUtils.isNotBlank(reqBody.getStart_date())) {
            specification = specification.and(SpecificationUtils.ge("statDate", Long.valueOf(reqBody.getStart_date())));
        } else if (StringUtils.isNotBlank(reqBody.getEnd_date())) {
            specification = specification.and(SpecificationUtils.le("statDate", Long.valueOf(reqBody.getEnd_date())));
        }

        // 获取统计信息
        List<NcovStatistics> dataResult = ncovStatisticsRepository.findAll(specification, sort);
        for (NcovStatistics one : dataResult) {
            GetNcovStatsResult oneResp = new GetNcovStatsResult();
            oneResp.setRegion_id(String.valueOf(one.getRegionId()));
            Region regionInfo = regionRepository.getRegionById(one.getRegionId());
            if (null == regionInfo) {
                System.out.println("Not find region info for ID:" + one.getRegionId());
                continue;
            }
            oneResp.setRegion_name(regionInfo.getName());
            oneResp.setDiag_increase_quantity(String.valueOf(one.getDiagIncreaseQuantity()));
            oneResp.setDiag_total_quantity(String.valueOf(one.getDiagTotalQuantity()));
            oneResp.setSusp_increase_quantity(String.valueOf(one.getSuspIncreaseQuantity()));
            oneResp.setSusp_total_quantity(String.valueOf(one.getSuspTotalQuantity()));
            oneResp.setRecv_increase_quantity(String.valueOf(one.getRecvIncreaseQuantity()));
            oneResp.setRecv_total_quantity(String.valueOf(one.getRecvTotalQuantity()));
            oneResp.setDeath_increase_quantity(String.valueOf(one.getDeathIncreaseQuantity()));
            oneResp.setDeath_total_quantity(String.valueOf(one.getDeathTotalQuantity()));
            oneResp.setStat_date(String.valueOf(one.getStatDate()));
            diagnosisStatsResultList.add(oneResp);
        }

        return diagnosisStatsResultList;
    }

    /**
     * 功能描述：设置ncov数据
     *
     * @param reqBody
     * @return
     * @throws Exception
     */
    public Object setNcovInfo(SetNcovInfoReq reqBody) throws Exception {
        if (StringUtils.isBlank(reqBody.getRegion_id()) || StringUtils.isBlank(reqBody.getStat_date())) {
            throw new Exception("参数缺失");
        }

        //获取已有记录
        NcovStatistics ncovStatistics = ncovStatisticsRepository.getNcovStatisticsByRegionIdAAndStatDate(Long.valueOf(reqBody.getRegion_id()),
                Long.valueOf(reqBody.getStat_date()));
        if (null == ncovStatistics) {
            ncovStatistics = new NcovStatistics();
            ncovStatistics.setRegionId(Long.valueOf(reqBody.getRegion_id()));
            ncovStatistics.setStatDate(Long.valueOf(reqBody.getStat_date()));
        }

        // 更新录入数据信息
        if (StringUtils.isNotBlank(reqBody.getDiag_increase_quantity())) {
            ncovStatistics.setDiagIncreaseQuantity(Long.valueOf(reqBody.getDiag_increase_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getDiag_total_quantity())) {
            ncovStatistics.setDiagTotalQuantity(Long.valueOf(reqBody.getDiag_total_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getSusp_increase_quantity())) {
            ncovStatistics.setSuspIncreaseQuantity(Long.valueOf(reqBody.getSusp_increase_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getSusp_total_quantity())) {
            ncovStatistics.setSuspTotalQuantity(Long.valueOf(reqBody.getSusp_total_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getRecv_increase_quantity())) {
            ncovStatistics.setRecvIncreaseQuantity(Long.valueOf(reqBody.getRecv_increase_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getRecv_total_quantity())) {
            ncovStatistics.setRecvTotalQuantity(Long.valueOf(reqBody.getRecv_total_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getDeath_increase_quantity())) {
            ncovStatistics.setDeathIncreaseQuantity(Long.valueOf(reqBody.getDeath_increase_quantity()));
        }
        if (StringUtils.isNotBlank(reqBody.getDeath_total_quantity())) {
            ncovStatistics.setDeathTotalQuantity(Long.valueOf(reqBody.getDeath_total_quantity()));
        }
        ncovStatistics = ncovStatisticsRepository.save(ncovStatistics);
        SetNcovInfoResult setNcovInfoResult = new SetNcovInfoResult();
        setNcovInfoResult.setRegion_id(String.valueOf(ncovStatistics.getRegionId()));
        setNcovInfoResult.setStat_date(String.valueOf(ncovStatistics.getStatDate()));
        setNcovInfoResult.setDiag_increase_quantity(String.valueOf(ncovStatistics.getDiagIncreaseQuantity()));
        setNcovInfoResult.setDiag_total_quantity(String.valueOf(ncovStatistics.getDiagTotalQuantity()));
        setNcovInfoResult.setSusp_increase_quantity(String.valueOf(ncovStatistics.getSuspIncreaseQuantity()));
        setNcovInfoResult.setSusp_total_quantity(String.valueOf(ncovStatistics.getSuspTotalQuantity()));
        setNcovInfoResult.setRecv_increase_quantity(String.valueOf(ncovStatistics.getRecvIncreaseQuantity()));
        setNcovInfoResult.setRecv_total_quantity(String.valueOf(ncovStatistics.getRecvTotalQuantity()));
        setNcovInfoResult.setDeath_increase_quantity(String.valueOf(ncovStatistics.getDeathIncreaseQuantity()));
        setNcovInfoResult.setDeath_total_quantity(String.valueOf(ncovStatistics.getDeathTotalQuantity()));
        return ncovStatistics;
    }

    /**
     * 功能描述：获取区域列表
     * @return
     * @throws Exception
     */
    public Object getRegionList() throws Exception {
        List<Region> dataResp = regionRepository.findAll();
        return dataResp;
    }
}
