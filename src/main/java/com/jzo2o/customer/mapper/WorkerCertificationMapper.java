package com.jzo2o.customer.mapper;

import java.util.List;

import com.jzo2o.customer.model.domain.WorkerCertification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.customer.model.dto.response.WorkerCertificationAuditResDTO;

/**
 * <p>
 * 服务人员认证信息表 Mapper 接口
 * </p>
 *
 * @author itcast
 * @since 2023-09-06
 */
public interface WorkerCertificationMapper extends BaseMapper<WorkerCertification> {

    /**
     * 查询服务人员认证信息
     * @return
     */
    List<WorkerCertificationAuditResDTO> queryWorkerCertification();

}
