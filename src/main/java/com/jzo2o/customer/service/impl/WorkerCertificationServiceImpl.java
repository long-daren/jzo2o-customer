package com.jzo2o.customer.service.impl;

import java.time.LocalDateTime;
import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.customer.enums.CertificationStatusEnum;
import com.jzo2o.customer.mapper.WorkerAuditMapper;
import com.jzo2o.customer.mapper.WorkerCertificationMapper;
import com.jzo2o.customer.model.domain.WorkerAudit;
import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.WorkerCertificationUpdateDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationAuditResDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationResDTO;
import com.jzo2o.customer.service.IWorkerCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageHelperUtils;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务人员认证信息表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-09-06
 */
@Service
public class WorkerCertificationServiceImpl extends ServiceImpl<WorkerCertificationMapper, WorkerCertification> implements IWorkerCertificationService {



    @Resource
    private WorkerCertificationMapper workerCertificationMapper;

    @Resource
    private WorkerAuditMapper workerAuditMapper;
    /**
     * 根据服务人员id更新
     *
     * @param workerCertificationUpdateDTO 服务人员认证更新模型
     */
    @Override
    public void updateById(WorkerCertificationUpdateDTO workerCertificationUpdateDTO) {
        LambdaUpdateWrapper<WorkerCertification> updateWrapper = Wrappers.<WorkerCertification>lambdaUpdate()
                .eq(WorkerCertification::getId, workerCertificationUpdateDTO.getId())
                .set(WorkerCertification::getCertificationStatus, workerCertificationUpdateDTO.getCertificationStatus())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getName()), WorkerCertification::getName, workerCertificationUpdateDTO.getName())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getIdCardNo()), WorkerCertification::getIdCardNo, workerCertificationUpdateDTO.getIdCardNo())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getFrontImg()), WorkerCertification::getFrontImg, workerCertificationUpdateDTO.getFrontImg())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getBackImg()), WorkerCertification::getBackImg, workerCertificationUpdateDTO.getBackImg())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getCertificationMaterial()), WorkerCertification::getCertificationMaterial, workerCertificationUpdateDTO.getCertificationMaterial())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getCertificationTime()), WorkerCertification::getCertificationTime, workerCertificationUpdateDTO.getCertificationTime());
        super.update(updateWrapper);
    }

    /**
     * 提交认证申请
     *
     * @param workerCertificationAuditAddReqDTO
     */
    @Override
    @Transactional
    public WorkerCertification  submitAuth(WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO) {
        Long workId = UserContext.currentUserId();
        WorkerCertification workerCertification = BeanUtils.toBean(workerCertificationAuditAddReqDTO, WorkerCertification.class);
        workerCertification.setId(workId);
        workerCertification.setCertificationStatus(CertificationStatusEnum.PROGRESSING.getStatus());
        //1.4.设置时间
        workerCertification.setCreateTime(LocalDateTime.now());
        workerCertification.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(workerCertification);
        WorkerCertification certification = baseMapper.selectById(workId);
        if (certification == null) {
            throw new BadRequestException("提交认证失败");
        }
        //2.修改agency_audit表
        WorkerAudit workerAudit = new WorkerAudit();
        workerAudit.setId(workId);
        workerAudit.setAuditStatus(0);
        workerAudit.setUpdateTime(LocalDateTime.now());
        workerAuditMapper.insert(workerAudit);
        return certification;
    }

    /**
     * 分页查询认证申请
     *
     * @param workerCertificationAuditPageQueryReqDTO
     * @return
     */
    @Override
    public PageResult<WorkerCertificationAuditResDTO> page(WorkerCertificationAuditPageQueryReqDTO workerCertificationAuditPageQueryReqDTO) {
        PageResult<WorkerCertificationAuditResDTO> pageResult = PageHelperUtils
            .selectPage(workerCertificationAuditPageQueryReqDTO, () -> workerCertificationMapper.queryWorkerCertification());
        return pageResult;
    }

    /**
     * 审核认证申请
     *
     * @param id
     * @param certificationStatus
     * @param rejectReason
     */
    @Override
    public void audit(Long id, Integer certificationStatus, String rejectReason) {
        //1.更新worker_certification表
        LambdaUpdateWrapper<WorkerCertification> updateWrapper = Wrappers.<WorkerCertification>lambdaUpdate()
            .eq(WorkerCertification::getId, id)
            .set(WorkerCertification::getCertificationStatus, certificationStatus)
            .set(WorkerCertification::getUpdateTime, LocalDateTime.now());
        if(certificationStatus == 2){
            updateWrapper=updateWrapper.set(WorkerCertification::getCertificationTime, LocalDateTime.now());
        }
        update(updateWrapper);
        //2.更新worker_audit表
        WorkerAudit workerAudit = workerAuditMapper.selectById(id);
        if(workerAudit == null) {
            workerAudit = new WorkerAudit();
            workerAudit.setId(id);
            workerAuditMapper.insert(workerAudit);
        }
        workerAudit.setAuditStatus(1);
        workerAudit.setRejectReason(rejectReason);
        workerAudit.setAuditTime(LocalDateTime.now());
        workerAudit.setUpdateTime(LocalDateTime.now());
        workerAudit.setAuditorId(UserContext.currentUserId());
        workerAudit.setAuditorName(UserContext.currentUser().getName());
        workerAuditMapper.updateById(workerAudit);
    }
}
