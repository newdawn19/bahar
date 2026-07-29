package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.order.UserOrderDto;
import com.bahar.common.param.RechargeParam;
import com.bahar.common.service.*;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtOrder;
import com.bahar.repository.model.MtStaff;
import com.bahar.repository.model.MtUser;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 余额接口controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/balance")
public class MerchantBalanceController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody RechargeParam rechargeParam) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtStaff staffInfo = null;
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtUser memberInfo = memberService.queryMemberById(rechargeParam.getMemberId());
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        if (!merchantId.equals(staffInfo.getMerchantId()) || !staffInfo.getMerchantId().equals(memberInfo.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        MtOrder mtOrder = orderService.doRecharge(request, rechargeParam);
        Boolean result = false;
        if (mtOrder != null) {
            UserOrderDto orderInfo = orderService.getOrderByOrderSn(mtOrder.getOrderSn());
            if (orderInfo != null) {
                result = paymentService.paymentCallback(orderInfo);
            }
        }
        return getSuccessResult(result);
    }
}
