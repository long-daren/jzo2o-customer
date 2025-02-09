package com.jzo2o.customer.controller.open;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.customer.model.dto.request.LoginForWorkReqDTO;
import com.jzo2o.customer.service.ILoginService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("ResetPasswordController")
@RequestMapping("/agency/serve-provider")
@Api(tags = "白名单接口 - 机构端重置密码")
public class ResetPasswordController {

    @Resource
    private ILoginService loginService;
    @PostMapping("/institution/resetPassword")
    @ApiOperation("服务人员/重置密码")
    public void resetPassword(@RequestBody LoginForWorkReqDTO loginForWorkReqDTO) {
        loginService.resetPassword(loginForWorkReqDTO);
    }
}
