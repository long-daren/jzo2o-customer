package com.jzo2o.customer.controller.operation;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationAuditResDTO;
import com.jzo2o.customer.service.IWorkerCertificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController("operationWorkerAuditController")
@RequestMapping("/operation/worker-certification-audit")
@Api(tags = "运营端 - 服务人员审核信息相关接口")
public class WorkerAuditController {

    @Resource
    private IWorkerCertificationService workerCertificationService;

    /**
     * 分页查询认证审核列表
     * @param workerCertificationAuditPageQueryReqDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询认证审核列表")
    public PageResult<WorkerCertificationAuditResDTO> page(WorkerCertificationAuditPageQueryReqDTO workerCertificationAuditPageQueryReqDTO) {
        return workerCertificationService.page(workerCertificationAuditPageQueryReqDTO);
    }

    @PutMapping("/audit/{id}")
    @ApiOperation("审核服务人员认证信息")
    public void audit(@PathVariable("id") Long id, @RequestParam("certificationStatus") Integer certificationStatus, @RequestParam("rejectReason") String rejectReason){
        workerCertificationService.audit(id,certificationStatus,rejectReason);
    }

}
