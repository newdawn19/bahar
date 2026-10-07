package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.member.PointDto;
import com.bahar.common.param.PointPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtPoint;

/**
 * 积分业务接口
 *
 * CopyRight https://www.bahar.cn
 */
public interface PointService extends IService<MtPoint> {

    /**
     * 分页查询积分列表
     *
     * @param pointPage
     * @return
     */
    PaginationResponse<PointDto> queryPointListByPagination(PointPage pointPage);

    /**
     * 添加积分
     *
     * @param  mtPoint
     * @throws BusinessCheckException
     * @return
     */
    void addPoint(MtPoint mtPoint) throws BusinessCheckException;

    /**
     * 转赠积分
     *
     * @param userId
     * @param mobile
     * @param amount
     * @param remark
     * @throws BusinessCheckException
     * @return
     */
    boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException;
}
