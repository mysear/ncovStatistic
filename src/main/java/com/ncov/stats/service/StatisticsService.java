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

import java.util.*;

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
        if (StringUtils.isBlank(reqBody.getRegion_id())) {
            return diagnosisStatsResultList;
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "statDate");
        Specification<NcovStatistics> specification = Specification.where(SpecificationUtils.ge("id", 0));

        // 区域ID，单独处理全国（含港澳台）和全国（含港澳台不含湖北的数据）
        if (StringUtils.isNotBlank(reqBody.getRegion_id())) {
            if ("36".equals(reqBody.getRegion_id())) {
                // 设置全国区域ID包含港澳台
                Set regionIdSet = new HashSet<>();
                regionIdSet.add(1);
                regionIdSet.add(33);
                regionIdSet.add(34);
                regionIdSet.add(35);
                specification = specification.and(SpecificationUtils.in("regionId", regionIdSet));
            } else if ("37".equals(reqBody.getRegion_id())) {
                // 设置全国区域ID包含港澳台但不含湖北
                Set regionIdSet = new HashSet<>();
                regionIdSet.add(1);
                regionIdSet.add(33);
                regionIdSet.add(34);
                regionIdSet.add(35);
                regionIdSet.add(18);
                specification = specification.and(SpecificationUtils.in("regionId", regionIdSet));
            } else {
                specification = specification.and(SpecificationUtils.equal("regionId",
                        Long.valueOf(reqBody.getRegion_id())));
            }
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
        if (StringUtils.isNotBlank(reqBody.getRegion_id()) && "36".equals(reqBody.getRegion_id())) {
            List<NcovStatistics> allAreaStatisticsList = new ArrayList<>();
            Map<Long, NcovStatistics> twStatisticsMap = new HashMap<>();
            Map<Long, NcovStatistics> hkStatisticsMap = new HashMap<>();
            Map<Long, NcovStatistics> moStatisticsMap = new HashMap<>();

            for (NcovStatistics one : dataResult) {
                if (1 == one.getRegionId()) {
                    allAreaStatisticsList.add(one);
                } else if (33 == one.getRegionId()) {
                    twStatisticsMap.put(one.getStatDate(), one);
                } else if (34 == one.getRegionId()) {
                    hkStatisticsMap.put(one.getStatDate(), one);
                } else if (35 == one.getRegionId()) {
                    moStatisticsMap.put(one.getStatDate(), one);
                }
            }

            String regionName = "";
            for (NcovStatistics one : allAreaStatisticsList) {
                Long dateVal = one.getStatDate();
                NcovStatistics dataEntry = one;

                NcovStatistics twData = twStatisticsMap.get(dateVal);
                NcovStatistics hkData = hkStatisticsMap.get(dateVal);
                NcovStatistics moData = moStatisticsMap.get(dateVal);

                GetNcovStatsResult oneResp = new GetNcovStatsResult();
                oneResp.setRegion_id(reqBody.getRegion_id());
                if (StringUtils.isBlank(regionName)) {
                    Region regionInfo = regionRepository.getRegionById(Long.valueOf(reqBody.getRegion_id()));
                    if (null != regionInfo) {
                        regionName = regionInfo.getName();
                    }
                }
                oneResp.setRegion_name(regionName);

                // 确诊增加数量
                long diag_inqty = dataEntry.getDiagIncreaseQuantity();
                if (null != twData && twData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += twData.getDiagIncreaseQuantity();
                }
                if (null != hkData && hkData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += hkData.getDiagIncreaseQuantity();
                }
                if (null != moData && moData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += moData.getDiagIncreaseQuantity();
                }
                oneResp.setDiag_increase_quantity(String.valueOf(diag_inqty));

                // 确诊总量
                long diag_allqty = dataEntry.getDiagTotalQuantity();
                if (null != twData && twData.getDiagTotalQuantity() > 0) {
                    diag_allqty += twData.getDiagTotalQuantity();
                }
                if (null != hkData && hkData.getDiagTotalQuantity() > 0) {
                    diag_allqty += hkData.getDiagTotalQuantity();
                }
                if (null != moData && moData.getDiagTotalQuantity() > 0) {
                    diag_allqty += moData.getDiagTotalQuantity();
                }
                oneResp.setDiag_total_quantity(String.valueOf(diag_allqty));

                // 疑似增加数量
                long susp_inqty = dataEntry.getSuspIncreaseQuantity();
                if (null != twData && twData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += twData.getSuspIncreaseQuantity();
                }
                if (null != hkData && hkData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += hkData.getSuspIncreaseQuantity();
                }
                if (null != moData && moData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += moData.getSuspIncreaseQuantity();
                }
                oneResp.setSusp_increase_quantity(String.valueOf(susp_inqty));

                // 疑似总量
                long susp_allqty = dataEntry.getSuspTotalQuantity();
                if (null != twData && twData.getSuspTotalQuantity() > 0) {
                    susp_allqty += twData.getSuspTotalQuantity();
                }
                if (null != hkData && hkData.getSuspTotalQuantity() > 0) {
                    susp_allqty += hkData.getSuspTotalQuantity();
                }
                if (null != moData && moData.getSuspTotalQuantity() > 0) {
                    susp_allqty += moData.getSuspTotalQuantity();
                }
                oneResp.setSusp_total_quantity(String.valueOf(susp_allqty));

                // 痊愈增加数量
                long recv_inqty = dataEntry.getRecvIncreaseQuantity();
                if (null != twData && twData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += twData.getRecvIncreaseQuantity();
                }
                if (null != hkData && hkData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += hkData.getRecvIncreaseQuantity();
                }
                if (null != moData && moData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += moData.getRecvIncreaseQuantity();
                }
                oneResp.setRecv_increase_quantity(String.valueOf(recv_inqty));

                // 痊愈总量
                long recv_allqty = dataEntry.getRecvTotalQuantity();
                if (null != twData && twData.getRecvTotalQuantity() > 0) {
                    recv_allqty += twData.getRecvTotalQuantity();
                }
                if (null != hkData && hkData.getRecvTotalQuantity() > 0) {
                    recv_allqty += hkData.getRecvTotalQuantity();
                }
                if (null != moData && moData.getRecvTotalQuantity() > 0) {
                    recv_allqty += moData.getRecvTotalQuantity();
                }
                oneResp.setRecv_total_quantity(String.valueOf(recv_allqty));

                // 死亡增加数量
                long death_inqty = dataEntry.getDeathIncreaseQuantity();
                if (null != twData && twData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += twData.getDeathIncreaseQuantity();
                }
                if (null != hkData && hkData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += hkData.getDeathIncreaseQuantity();
                }
                if (null != moData && moData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += moData.getDeathIncreaseQuantity();
                }
                oneResp.setDeath_increase_quantity(String.valueOf(death_inqty));

                // 死亡总量
                long death_allqty = dataEntry.getDeathTotalQuantity();
                if (null != twData && twData.getDeathTotalQuantity() > 0) {
                    death_allqty += twData.getDeathTotalQuantity();
                }
                if (null != hkData && hkData.getDeathTotalQuantity() > 0) {
                    death_allqty += hkData.getDeathTotalQuantity();
                }
                if (null != moData && moData.getDeathTotalQuantity() > 0) {
                    death_allqty += moData.getDeathTotalQuantity();
                }
                oneResp.setDeath_total_quantity(String.valueOf(death_allqty));

                // 统计日期
                oneResp.setStat_date(String.valueOf(dataEntry.getStatDate()));
                diagnosisStatsResultList.add(oneResp);
            }
        } else if (StringUtils.isNotBlank(reqBody.getRegion_id()) && "37".equals(reqBody.getRegion_id())) {
            List<NcovStatistics> allAreaStatisticsList = new ArrayList<>();
            Map<Long, NcovStatistics> twStatisticsMap = new HashMap<>();
            Map<Long, NcovStatistics> hkStatisticsMap = new HashMap<>();
            Map<Long, NcovStatistics> moStatisticsMap = new HashMap<>();
            Map<Long, NcovStatistics> hbStatisticsMap = new HashMap<>();

            for (NcovStatistics one : dataResult) {
                if (1 == one.getRegionId()) {
                    allAreaStatisticsList.add(one);
                } else if (33 == one.getRegionId()) {
                    twStatisticsMap.put(one.getStatDate(), one);
                } else if (34 == one.getRegionId()) {
                    hkStatisticsMap.put(one.getStatDate(), one);
                } else if (35 == one.getRegionId()) {
                    moStatisticsMap.put(one.getStatDate(), one);
                } else if (18 == one.getRegionId()) {
                    hbStatisticsMap.put(one.getStatDate(), one);
                }
            }

            String regionName = "";
            for (NcovStatistics one : allAreaStatisticsList) {
                Long dateVal = one.getStatDate();
                NcovStatistics dataEntry = one;

                NcovStatistics twData = twStatisticsMap.get(dateVal);
                NcovStatistics hkData = hkStatisticsMap.get(dateVal);
                NcovStatistics moData = moStatisticsMap.get(dateVal);
                NcovStatistics hbData = hbStatisticsMap.get(dateVal);

                GetNcovStatsResult oneResp = new GetNcovStatsResult();
                oneResp.setRegion_id(reqBody.getRegion_id());
                if (StringUtils.isBlank(regionName)) {
                    Region regionInfo = regionRepository.getRegionById(Long.valueOf(reqBody.getRegion_id()));
                    if (null != regionInfo) {
                        regionName = regionInfo.getName();
                    }
                }
                oneResp.setRegion_name(regionName);

                // 确诊增加数量
                long diag_inqty = dataEntry.getDiagIncreaseQuantity();
                if (null != twData && twData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += twData.getDiagIncreaseQuantity();
                }
                if (null != hkData && hkData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += hkData.getDiagIncreaseQuantity();
                }
                if (null != moData && moData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty += moData.getDiagIncreaseQuantity();
                }
                if (null != hbData && hbData.getDiagIncreaseQuantity() > 0) {
                    diag_inqty -= hbData.getDiagIncreaseQuantity();
                }
                oneResp.setDiag_increase_quantity(String.valueOf(diag_inqty));

                // 确诊总量
                long diag_allqty = dataEntry.getDiagTotalQuantity();
                if (null != twData && twData.getDiagTotalQuantity() > 0) {
                    diag_allqty += twData.getDiagTotalQuantity();
                }
                if (null != hkData && hkData.getDiagTotalQuantity() > 0) {
                    diag_allqty += hkData.getDiagTotalQuantity();
                }
                if (null != moData && moData.getDiagTotalQuantity() > 0) {
                    diag_allqty += moData.getDiagTotalQuantity();
                }
                if (null != hbData && hbData.getDiagTotalQuantity() > 0) {
                    diag_allqty -= hbData.getDiagTotalQuantity();
                }
                oneResp.setDiag_total_quantity(String.valueOf(diag_allqty));

                // 疑似增加数量
                long susp_inqty = dataEntry.getSuspIncreaseQuantity();
                if (null != twData && twData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += twData.getSuspIncreaseQuantity();
                }
                if (null != hkData && hkData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += hkData.getSuspIncreaseQuantity();
                }
                if (null != moData && moData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty += moData.getSuspIncreaseQuantity();
                }
                if (null != hbData && hbData.getSuspIncreaseQuantity() > 0) {
                    susp_inqty -= hbData.getSuspIncreaseQuantity();
                }
                // 未统计湖北疑似增加数量，此数据无效
                oneResp.setSusp_increase_quantity(String.valueOf(-1));

                // 疑似总量
                long susp_allqty = dataEntry.getSuspTotalQuantity();
                if (null != twData && twData.getSuspTotalQuantity() > 0) {
                    susp_allqty += twData.getSuspTotalQuantity();
                }
                if (null != hkData && hkData.getSuspTotalQuantity() > 0) {
                    susp_allqty += hkData.getSuspTotalQuantity();
                }
                if (null != moData && moData.getSuspTotalQuantity() > 0) {
                    susp_allqty += moData.getSuspTotalQuantity();
                }
                if (null != hbData && hbData.getSuspTotalQuantity() > 0) {
                    susp_allqty -= hbData.getSuspTotalQuantity();
                }
                // 未统计湖北疑似总量，此数据无效
                oneResp.setSusp_total_quantity(String.valueOf(-1));

                // 痊愈增加数量
                long recv_inqty = dataEntry.getRecvIncreaseQuantity();
                if (null != twData && twData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += twData.getRecvIncreaseQuantity();
                }
                if (null != hkData && hkData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += hkData.getRecvIncreaseQuantity();
                }
                if (null != moData && moData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty += moData.getRecvIncreaseQuantity();
                }
                if (null != hbData && hbData.getRecvIncreaseQuantity() > 0) {
                    recv_inqty -= hbData.getRecvIncreaseQuantity();
                }
                oneResp.setRecv_increase_quantity(String.valueOf(recv_inqty));

                // 痊愈总量
                long recv_allqty = dataEntry.getRecvTotalQuantity();
                if (null != twData && twData.getRecvTotalQuantity() > 0) {
                    recv_allqty += twData.getRecvTotalQuantity();
                }
                if (null != hkData && hkData.getRecvTotalQuantity() > 0) {
                    recv_allqty += hkData.getRecvTotalQuantity();
                }
                if (null != moData && moData.getRecvTotalQuantity() > 0) {
                    recv_allqty += moData.getRecvTotalQuantity();
                }
                if (null != hbData && hbData.getRecvTotalQuantity() > 0) {
                    recv_allqty -= hbData.getRecvTotalQuantity();
                }
                oneResp.setRecv_total_quantity(String.valueOf(recv_allqty));

                // 死亡增加数量
                long death_inqty = dataEntry.getDeathIncreaseQuantity();
                if (null != twData && twData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += twData.getDeathIncreaseQuantity();
                }
                if (null != hkData && hkData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += hkData.getDeathIncreaseQuantity();
                }
                if (null != moData && moData.getDeathIncreaseQuantity() > 0) {
                    death_inqty += moData.getDeathIncreaseQuantity();
                }
                if (null != hbData && hbData.getDeathIncreaseQuantity() > 0) {
                    death_inqty -= hbData.getDeathIncreaseQuantity();
                }
                oneResp.setDeath_increase_quantity(String.valueOf(death_inqty));

                // 死亡总量
                long death_allqty = dataEntry.getDeathTotalQuantity();
                if (null != twData && twData.getDeathTotalQuantity() > 0) {
                    death_allqty += twData.getDeathTotalQuantity();
                }
                if (null != hkData && hkData.getDeathTotalQuantity() > 0) {
                    death_allqty += hkData.getDeathTotalQuantity();
                }
                if (null != moData && moData.getDeathTotalQuantity() > 0) {
                    death_allqty += moData.getDeathTotalQuantity();
                }
                if (null != hbData && hbData.getDeathTotalQuantity() > 0) {
                    death_allqty -= hbData.getDeathTotalQuantity();
                }
                oneResp.setDeath_total_quantity(String.valueOf(death_allqty));

                // 统计日期
                oneResp.setStat_date(String.valueOf(dataEntry.getStatDate()));
                diagnosisStatsResultList.add(oneResp);
            }
        } else {
            String regionName = "";
            for (NcovStatistics one : dataResult) {
                GetNcovStatsResult oneResp = new GetNcovStatsResult();
                oneResp.setRegion_id(String.valueOf(one.getRegionId()));
                if (StringUtils.isBlank(regionName)) {
                    Region regionInfo = regionRepository.getRegionById(Long.valueOf(reqBody.getRegion_id()));
                    if (null != regionInfo) {
                        regionName = regionInfo.getName();
                    }
                }
                oneResp.setRegion_name(regionName);
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
     *
     * @return
     * @throws Exception
     */
    public Object getRegionList() throws Exception {
        List<Region> dataResp = regionRepository.findAll();
        return dataResp;
    }
}
