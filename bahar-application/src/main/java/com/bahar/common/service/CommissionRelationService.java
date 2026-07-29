package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.commission.CommissionRelationDto;
import com.bahar.common.param.CommissionRelationPage;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtCommissionRelation;
import com.bahar.repository.model.MtUser;

/**
 * 分销提成关系业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface CommissionRelationService extends IService<MtCommissionRelation> {

    /**
     * 分页查询分佣关系列表
     *
     * @param commissionRelationPage
     * @return
     */
    PaginationResponse<CommissionRelationDto> queryRelationByPagination(CommissionRelationPage commissionRelationPage);

    /**
     * 设置分销提成关系
     *
     * @param  userInfo 会员信息
     * @param  shareId 分享者ID
     * @retrurn
     */
    void setCommissionRelation(MtUser userInfo, String shareId);
}
