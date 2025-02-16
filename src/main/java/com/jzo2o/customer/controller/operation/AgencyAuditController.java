package com.jzo2o.customer.controller.operation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.service.IAgencyCertificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("operationAgencyAuditController")
@RequestMapping("/operation/agency-certification-audit")
@Api(tags = "运营端 - 机构审核信息相关接口")
public class AgencyAuditController {

    @Resource
    private IAgencyCertificationService agencyCertificationService;
    @GetMapping("/page")
    @ApiOperation("分页查询认证审核列表")
    public PageResult<AgencyCertificationAuditResDTO> page(AgencyCertificationAuditPageQueryReqDTO agencyCertificationAuditPageQueryReqDTO) {
        return agencyCertificationService.page(agencyCertificationAuditPageQueryReqDTO);
    }

    @PutMapping("/audit/{id}")
    @ApiOperation("审核机构认证信息")
    public void audit(@PathVariable("id") Long id, @RequestParam("certificationStatus") Integer certificationStatus, @RequestParam("rejectReason") String rejectReason){
        agencyCertificationService.audit(id,certificationStatus,rejectReason);
    }

}
