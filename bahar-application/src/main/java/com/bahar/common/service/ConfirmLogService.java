package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.coupon.ConfirmLogDto;
import com.bahar.common.param.ConfirmLogPage;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtConfirmLog;

import java.util.Date;
import java.util.List;

/**
 * 核销记录业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface ConfirmLogService extends IService<MtConfirmLog> {

    /**
     * 分页查询会员卡券核销列表
     *
     * @param confirmLogPage
     * @return
     */
    PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(ConfirmLogPage confirmLogPage);

    /**
     * 获取卡券核销次数
     * @param userCouponId
     * @return
     * */
    Long getConfirmNum(Integer userCouponId);

    /**
     * 获取卡券核销列表
     * @param userCouponId
     * @return
     * */
    List<MtConfirmLog> getConfirmList(Integer userCouponId);

    /**
     * 获取核销总数
     * */
    Long getConfirmCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime);
}
