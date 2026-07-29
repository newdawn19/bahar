package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.order.RefundDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.RefundPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.ResponseObject;
import com.bahar.module.clientApi.request.RefundListRequest;
import com.bahar.repository.model.MtRefund;

import java.util.Date;

/**
 * 售后业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface RefundService extends IService<MtRefund> {

    /**
     * 分页查询列表
     *
     * @param refundPage
     * @return
     */
    PaginationResponse<RefundDto> getRefundListByPagination(RefundPage refundPage);

    /**
     * 获取用户售后订单
     *
     * @param param 查询参数
     * @return
     * */
    ResponseObject getUserRefundList(RefundListRequest param);

    /**
     * 创建售后订单
     *
     * @param refundDto
     * @return
     */
    MtRefund createRefund(RefundDto refundDto);

    /**
     * 根据ID获取售后订单信息
     *
     * @param id ID
     * @return
     */
    RefundDto getRefundById(Integer id);

    /**
     * 根据订单ID获取售后订单信息
     *
     * @param  orderId
     * @return
     */
    MtRefund getRefundByOrderId(Integer orderId);

    /**
     * 更新售后订单
     *
     * @param  refundDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * */
    MtRefund updateRefund(RefundDto refundDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 同意售后订单
     *
     * @param  refundDto
     * @throws BusinessCheckException
     * */
    MtRefund agreeRefund(RefundDto refundDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 发起退款
     *
     * @param orderId 订单号
     * @param refundAmount 退款金额
     * @param remark 备注
     * @param accountInfo 操作人信息
     * throws BusinessCheckException;
     * */
    Boolean doRefund(Integer orderId, String refundAmount, String remark, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取售后订单总数
     *
     * @param beginTime
     * @param endTime
     * @return
     * */
    Long getRefundCount(Date beginTime, Date endTime);
}
