package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;

/**
 * <p>
 * 个人银行账户 服务类
 * </p>
 *
 * @author itcast
 * @since 2025-02-09
 */
public interface IBankAccountService extends IService<BankAccount> {

    BankAccount saveOrUpdate(BankAccountUpsertReqDTO bankAccountUpsertReqDTO, Integer type);


}