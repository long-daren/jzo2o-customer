package com.jzo2o.customer.controller.agency;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("agencyBankAccountController")
@RequestMapping("/agency/bank-account")
@Api(tags = "机构端 - 银行账户相关接口")
public class BankAccountController {
    @Resource
    private IBankAccountService bankAccountService;

    /**
     * 新增或更新银行账户
     * @param bankAccountUpsertReqDTO
     */
    @PostMapping
    @ApiOperation("新增或更新银行账户")
    public void saveOrUpdate(@RequestBody BankAccountUpsertReqDTO bankAccountUpsertReqDTO) {
        // 机构端
        Integer type=3;
        bankAccountService.saveOrUpdate(bankAccountUpsertReqDTO,type);
    }

    /**
     * 获取当前用户银行账户
     * @return
     */
    @GetMapping("currentUserBankAccount")
    @ApiOperation("获取当前用户银行账户")
    public BankAccount currentUserBankAccount() {
        // 机构端
        Integer type=3;
        return bankAccountService.currentUserBankAccount(type);
    }
}
