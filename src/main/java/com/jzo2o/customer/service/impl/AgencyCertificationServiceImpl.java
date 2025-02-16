package com.jzo2o.customer.service.impl;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.customer.enums.CertificationStatusEnum;
import com.jzo2o.customer.mapper.AgencyAuditMapper;
import com.jzo2o.customer.mapper.AgencyCertificationMapper;
import com.jzo2o.customer.model.domain.AgencyAudit;
import com.jzo2o.customer.model.domain.AgencyCertification;
import com.jzo2o.customer.model.dto.AgencyCertificationUpdateDTO;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.service.IAgencyCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageHelperUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 机构认证信息表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-09-06
 */
@Service
public class AgencyCertificationServiceImpl extends ServiceImpl<AgencyCertificationMapper, AgencyCertification> implements IAgencyCertificationService {


    @Resource
    private AgencyAuditMapper agencyAuditMapper;

    /**
     * 根据机构id更新
     *
     * @param agencyCertificationUpdateDTO 机构认证更新模型
     */
    @Override
    public void updateByServeProviderId(AgencyCertificationUpdateDTO agencyCertificationUpdateDTO) {
        LambdaUpdateWrapper<AgencyCertification> updateWrapper = Wrappers.<AgencyCertification>lambdaUpdate()
                .eq(AgencyCertification::getId, agencyCertificationUpdateDTO.getId())
                .set(AgencyCertification::getCertificationStatus, agencyCertificationUpdateDTO.getCertificationStatus())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getName()), AgencyCertification::getName, agencyCertificationUpdateDTO.getName())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getIdNumber()), AgencyCertification::getIdNumber, agencyCertificationUpdateDTO.getIdNumber())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getLegalPersonName()), AgencyCertification::getLegalPersonName, agencyCertificationUpdateDTO.getLegalPersonName())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getLegalPersonIdCardNo()), AgencyCertification::getLegalPersonIdCardNo, agencyCertificationUpdateDTO.getLegalPersonIdCardNo())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getBusinessLicense()), AgencyCertification::getBusinessLicense, agencyCertificationUpdateDTO.getBusinessLicense())
                .set(ObjectUtil.isNotEmpty(agencyCertificationUpdateDTO.getCertificationTime()), AgencyCertification::getCertificationTime, agencyCertificationUpdateDTO.getCertificationTime());
        super.update(updateWrapper);
    }

    @Override
    public AgencyCertification submitAuth(AgencyCertificationAuditAddReqDTO agencyCertificationAuditAddReqDTO) {
        Long agencyId = UserContext.currentUserId();
        AgencyCertification agencyCertification = BeanUtils.toBean(agencyCertificationAuditAddReqDTO, AgencyCertification.class);
        agencyCertification.setId(agencyId);
        agencyCertification.setCertificationStatus(CertificationStatusEnum.PROGRESSING.getStatus());
        //1.4.设置时间
        agencyCertification.setCreateTime(LocalDateTime.now());
        agencyCertification.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(agencyCertification);
        AgencyCertification certification = baseMapper.selectById(agencyId);
        if (certification == null) {
            throw new BadRequestException("提交认证失败");
        }
        //2.修改agency_audit表
        AgencyAudit agencyAudit = new AgencyAudit();
        agencyAudit.setId(agencyId);
        agencyAudit.setAuditStatus(0);
        agencyAudit.setUpdateTime(LocalDateTime.now());
        agencyAuditMapper.insert(agencyAudit);
        return certification;
    }

    /**
     * 分页查询认证审核列表
     *
     * @param agencyCertificationAuditPageQueryReqDTO
     * @return
     */
    @Override
    public PageResult<AgencyCertificationAuditResDTO> page(AgencyCertificationAuditPageQueryReqDTO agencyCertificationAuditPageQueryReqDTO) {
        PageResult<AgencyCertificationAuditResDTO> pageResult  = PageHelperUtils.selectPage(agencyCertificationAuditPageQueryReqDTO, () -> baseMapper.queryAgencyCertification());
        return pageResult ;
    }

    /**
     * 审核机构认证信息
     *
     * @param id
     * @param certificationStatus
     * @param rejectReason
     */
    @Override
    @Transactional
    public void audit(Long id, Integer certificationStatus, String rejectReason) {
        //1.更新worker_certification表
        LambdaUpdateWrapper<AgencyCertification> updateWrapper = Wrappers.<AgencyCertification>lambdaUpdate()
                .eq(AgencyCertification::getId, id)
                .set(AgencyCertification::getCertificationStatus, certificationStatus)
                .set(AgencyCertification::getUpdateTime, LocalDateTime.now());
        if (certificationStatus == CertificationStatusEnum.SUCCESS.getStatus()) {
            updateWrapper.set(AgencyCertification::getCertificationTime, LocalDateTime.now());
        }
        update(updateWrapper);

        //2.更新worker_audit表
        AgencyAudit agencyAudit = agencyAuditMapper.selectById(id);
        if(agencyAudit == null) {
            agencyAudit = new AgencyAudit();
            agencyAudit.setId(id);
            agencyAuditMapper.insert(agencyAudit);
        }
        agencyAudit.setAuditStatus(1);
        agencyAudit.setRejectReason(rejectReason);
        agencyAudit.setAuditTime(LocalDateTime.now());
        agencyAudit.setUpdateTime(LocalDateTime.now());
        agencyAudit.setAuditorId(UserContext.currentUserId());
        agencyAudit.setAuditorName(UserContext.currentUser().getName());
        agencyAuditMapper.updateById(agencyAudit);
    }

}
