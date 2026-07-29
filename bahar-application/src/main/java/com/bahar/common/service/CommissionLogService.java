package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.commission.CommissionLogDto;
import com.bahar.common.dto.commission.CommissionOverviewDto;
import com.bahar.common.param.CommissionLogPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.module.backendApi.request.CommissionLogRequest;
import com.bahar.repository.model.MtCommissionLog;

/**
 * 分销提成记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface CommissionLogService extends IService<MtCommissionLog> {

    /**
     * 分页查询列表
     *
     * @param commissionLogPage
     * @return
     */
    PaginationResponse<CommissionLogDto> queryCommissionLogByPagination(CommissionLogPage commissionLogPage);

    /**
     * 获取佣金概览数据
     *
     * @param userId 会员ID
     * @return
     */
    CommissionOverviewDto getCommissionOverview(Integer userId);

    /**
     * 计算订单分销提成
     *
     * @param  orderId 订单ID
     * @return
     */
    void calculateCommission(Integer orderId);

    /**
     * 根据ID获取记录信息
     *
     * @param  id 记录ID
     * @return
     */
    CommissionLogDto queryCommissionLogById(Integer id);

    /**
     * 更新分销提成记录
     *
     * @param requestParam 请求参数
     * @throws BusinessCheckException
     * @return
     */
    void updateCommissionLog(CommissionLogRequest requestParam) throws BusinessCheckException;
}
