package com.ncov.stats.repository;

import com.ncov.stats.entity.NcovStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述: 确诊病例统计信息
 *
 * @author : contact@vector.link
 */

@Repository
public interface NcovStatisticsRepository extends JpaRepository<NcovStatistics, Long>, JpaSpecificationExecutor<NcovStatistics> {
    @Query(value = "select bean from NcovStatistics bean where bean.regionId = ?1")
    List<NcovStatistics> getNcovStatisticsByRegionId(long regionId);

    @Query(value = "select bean from NcovStatistics bean where bean.regionId = ?1 and bean.statDate = ?2")
    NcovStatistics getNcovStatisticsByRegionIdAAndStatDate(long regionId, long statDate);
}
