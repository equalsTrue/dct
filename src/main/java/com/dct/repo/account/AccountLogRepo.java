package com.dct.repo.account;

import com.dct.model.dct.AccountLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/11 16:13
 */
public interface AccountLogRepo extends JpaRepository<AccountLogModel,String>, JpaSpecificationExecutor<AccountLogModel> {

    @Query("SELECT DISTINCT t.creator FROM AccountLogModel t WHERE t.creator IS NOT NULL")
    List<String> findAllHandles();

    @Query("SELECT DISTINCT t.uid FROM AccountLogModel t WHERE t.uid IS NOT NULL")
    List<String> findAllUid();
}
