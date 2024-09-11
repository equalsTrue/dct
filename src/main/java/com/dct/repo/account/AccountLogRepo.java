package com.dct.repo.account;

import com.dct.model.dct.AccountLogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @program dct
 * @description:
 * @author: lichen
 * @create: 2024/09/11 16:13
 */
public interface AccountLogRepo extends JpaRepository<AccountLogModel,String>, JpaSpecificationExecutor<AccountLogModel> {
}
