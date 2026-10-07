package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.member.BalanceDto;
import com.bahar.common.param.BalancePage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * CopyRight https://www.bahar.cn
 */
public interface BalanceService extends IService<MtBalance> {

    /**
     * 分页查询余额列表
     *
     * @param balancePage
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(BalancePage balancePage);

    /**
     * 添加余额记录
     *
     * @param reqDto
     * @param updateBalance
     * @throws BusinessCheckException
     * @return
     */
    Boolean addBalance(MtBalance reqDto, Boolean updateBalance) throws BusinessCheckException;

    /**
     * 发放余额
     *
     * @param accountInfo 账号信息
     * @param object 发放对象，all全部
     * @param userIds 会员ID
     * @param amount 发放金额
     * @param remark 备注
     * @throws BusinessCheckException
     * @return
     */
    void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn);
}
