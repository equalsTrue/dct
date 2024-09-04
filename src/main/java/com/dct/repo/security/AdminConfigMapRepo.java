package com.dct.repo.security;

import com.dct.model.dct.AdminConfigMapModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 *
 * @author david
 */
public interface AdminConfigMapRepo extends JpaRepository<AdminConfigMapModel, String>,
        JpaSpecificationExecutor<AdminConfigMapModel> {

    /**
     * 精确查找配置
     * @param configName
     * @return
     */
    List<AdminConfigMapModel> findAllByConfigNameOrderByItemKeyAsc(String configName);

}
