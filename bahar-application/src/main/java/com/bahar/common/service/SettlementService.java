package com.bahar.common.service;

import com.bahar.common.dto.order.SettlementDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.SettlementPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.module.backendApi.request.SettlementRequest;
import com.bahar.repository.model.MtSettlement;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface SettlementService {

    /**
     * 分页查询结算列表
     *
     * @param settlementPage
     * @return
     */
    PaginationResponse<MtSettlement> querySettlementListByPagination(SettlementPage settlementPage);

    /**
     * 提交结算
     *
     * @param  requestParam
     * @throws BusinessCheckException
     * @return
     */
    Boolean submitSettlement(SettlementRequest requestParam) throws BusinessCheckException;

    /**
     * 结算确认
     *
     * @param  settlementId
     * @param  accountInfo
     * @throws BusinessCheckException
     * @return
     */
    Boolean doConfirm(Integer settlementId, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取结算详情
     *
     * @param settlementId
     * @param page
     * @param pageSize
     * @return
     * */
    SettlementDto getSettlementInfo(Integer settlementId, Integer page, Integer pageSize) throws BusinessCheckException;
}
