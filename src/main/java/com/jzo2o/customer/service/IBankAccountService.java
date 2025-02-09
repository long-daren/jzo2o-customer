package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;

/**
 * <p>
 * 个人银行账户 服务类
 * </p>
 *
 * @author itcast
 * @since 2025-02-09
 */
public interface IBankAccountService extends IService<BankAccount> {

    /**
     * 新增或更新银行账户
     * @param bankAccountUpsertReqDTO
     * @param type
     * @return
     */
    BankAccount saveOrUpdate(BankAccountUpsertReqDTO bankAccountUpsertReqDTO, Integer type);


    /**
     * 获取当前用户的银行账户信息
     * @param type
     * @return
     */
    BankAccount currentUserBankAccount(Integer type);


}