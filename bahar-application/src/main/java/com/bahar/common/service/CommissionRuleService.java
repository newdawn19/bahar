package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.commission.CommissionRuleDto;
import com.bahar.common.param.CommissionRulePage;
import com.bahar.common.param.CommissionRuleParam;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtCommissionRule;

/**
 * 分销提成规则业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface CommissionRuleService extends IService<MtCommissionRule> {

    /**
     * 分页查询列表
     *
     * @param commissionRulePage
     * @return
     */
    PaginationResponse<MtCommissionRule> queryDataByPagination(CommissionRulePage commissionRulePage);

    /**
     * 添加分佣提成规则
     *
     * @param  commissionRule
     * @throws BusinessCheckException
     */
    MtCommissionRule addCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

    /**
     * 根据ID获取规则信息
     *
     * @param  id
     * @return
     */
    CommissionRuleDto queryCommissionRuleById(Integer id);

    /**
     * 更新分佣提成规则
     *
     * @param  commissionRule
     * @return
     * */
    MtCommissionRule updateCommissionRule(CommissionRuleParam commissionRule) throws BusinessCheckException;

}
