package com.jzo2o.customer.controller.open;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.common.constants.UserType;
import com.jzo2o.customer.model.dto.request.LoginForWorkReqDTO;
import com.jzo2o.customer.model.dto.response.LoginResDTO;
import com.jzo2o.customer.service.ILoginService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("RegisterController")
@RequestMapping("/open/serve-provider")
@Api(tags = "白名单接口 - 机构端注册接口")
public class RegisterController {

    @Resource
    private ILoginService loginService;
    @PostMapping("/institution/register")
    @ApiOperation("服务人员/机构人员注册接口")
    public void register(@RequestBody LoginForWorkReqDTO loginForWorkReqDTO) {
        loginService.registry(loginForWorkReqDTO);
    }

}
