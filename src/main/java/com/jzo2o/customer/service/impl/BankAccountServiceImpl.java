package com.jzo2o.customer.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.customer.mapper.BankAccountMapper;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.service.IBankAccountService;
import com.jzo2o.mvc.utils.UserContext;

/**
 * <p>
 * 个人银行账户 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2025-02-09
 */
@Service
public class BankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccount> implements IBankAccountService {

    @Override
    public BankAccount saveOrUpdate(BankAccountUpsertReqDTO bankAccountUpsertReqDTO, Integer type) {
        Long userId = UserContext.currentUserId();
        BankAccount bankAccount = BeanUtils.toBean(bankAccountUpsertReqDTO, BankAccount.class);
        bankAccount.setUserId(userId);
        bankAccount.setUserType(type);
        saveOrUpdate(bankAccount);
        return bankAccount;
    }

    /**
     * 获取当前用户的银行账户信息
     *
     * @param type
     * @return
     */
    @Override
    public BankAccount currentUserBankAccount(Integer type) {
        Long userId = UserContext.currentUserId();
        BankAccount bankAccount = lambdaQuery().eq(BankAccount::getUserId, userId).eq(BankAccount::getUserType, type).one();
        if (bankAccount == null) {
           throw new BadRequestException("您还未开通银行账户");
        }
        return bankAccount;
    }
}