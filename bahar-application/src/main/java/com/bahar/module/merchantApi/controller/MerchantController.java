package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.merchant.StaffDto;
import com.bahar.common.service.ConfirmLogService;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.OrderService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.DateUtil;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户相关controller
 *
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-商户信息相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/merchant")
public class MerchantController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 卡券核销记录服务接口
     * */
    private ConfirmLogService confirmLogService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 查询商户信息
     * 商户端首页（merchantPages/index.vue）进来第一件事就调这个接口，
     * 返回当前登录会员绑定的员工信息 + 今日经营概览。
     */
    @ApiOperation(value = "查询商户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info() throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (null == userInfo) {
            return getFailureResult(1001, "用户未登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", mtUser);

        // 按手机号判断是否为商户员工（与 ClientUserController 的 isMerchant 同一套逻辑）
        StaffDto staffInfo = staffService.getStaffInfoByMobile(mtUser.getMobile());
        if (null == staffInfo) {
            return getFailureResult(1002, "您的帐号不是商户，没有操作权限");
        }

        outParams.put("confirmInfo", staffInfo);

        // 总收款额
        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();
        BigDecimal payMoney = orderService.getPayMoney(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("payMoney", payMoney);

        // 总会员数
        Long userCount = memberService.getUserCount(staffInfo.getMerchantId(), staffInfo.getStoreId());
        outParams.put("userCount", userCount);

        // 今日订单数
        BigDecimal orderCount = orderService.getOrderCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("orderCount", orderCount);

        // 核销券数
        Long confirmCount = confirmLogService.getConfirmCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("couponCount", confirmCount);

        // 今日活跃会员数
        Long todayUser = memberService.getActiveUserCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("todayUser", todayUser);

        return getSuccessResult(outParams);
    }
}
