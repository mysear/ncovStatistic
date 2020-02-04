package com.ncov.stats.repository;

import com.ncov.stats.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 功能描述: 区域信息
 * @author : contact@vector.link
 */

@Repository
public interface RegionRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {
    @Query(value = "select bean from Region bean where bean.name = ?1")
    Region getRegionByName(String name);

    @Query(value = "select bean from Region bean where bean.id = ?1")
    Region getRegionById(long regionId);
}
