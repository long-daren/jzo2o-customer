package com.jzo2o.customer.controller.consumer;


import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.service.IBankAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("workerBankAccountController")
@RequestMapping("/worker/bank-account")
@Api(tags = "服务端 - 银行账户相关接口")
public class BankAccountController {
    @Resource
    private IBankAccountService bankAccountService;

    @PostMapping
    @ApiOperation("新增或更新银行账户")
    public void saveOrUpdate(@RequestBody BankAccountUpsertReqDTO bankAccountUpsertReqDTO) {
        // 服务端
        Integer type=2;
        bankAccountService.saveOrUpdate(bankAccountUpsertReqDTO,type);
    }

    @GetMapping("/currentUserBankAccount")
    @ApiOperation("获取当前用户的银行账户信息")
    public BankAccount currentUserBankAccount() {
        // 服务端
        Integer type=2;
        return bankAccountService.currentUserBankAccount(type);
    }
}

