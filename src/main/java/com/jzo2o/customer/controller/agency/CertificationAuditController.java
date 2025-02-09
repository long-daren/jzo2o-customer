package com.jzo2o.customer.controller.agency;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.service.IAgencyCertificationService;
import com.jzo2o.customer.service.IWorkerCertificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("agencyCertificationAuditController")
@RequestMapping("/agency/agency-certification-audit")
@Api(tags = "机构端 - 认证审核相关接口")
public class CertificationAuditController {
    @Resource
    private IAgencyCertificationService agencyCertificationService;

    @PostMapping
    @ApiOperation("机构端提交认证申请")
    public void submitAuth(@RequestBody AgencyCertificationAuditAddReqDTO agencyCertificationAuditAddReqDTO) {
        agencyCertificationService.submitAuth(agencyCertificationAuditAddReqDTO);
    }
}
