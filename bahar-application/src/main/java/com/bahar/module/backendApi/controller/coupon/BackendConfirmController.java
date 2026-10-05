package com.bahar.module.backendApi.controller.coupon;

import com.bahar.common.dto.common.ParamDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.CouponExpireTypeEnum;
import com.bahar.common.enums.CouponTypeEnum;
import com.bahar.common.enums.UserCouponStatusEnum;
import com.bahar.common.service.ConfirmLogService;
import com.bahar.common.service.CouponService;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.UserCouponService;
import com.bahar.common.util.DateUtil;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.mapper.MtUserCouponMapper;
import com.bahar.repository.model.MtCoupon;
import com.bahar.repository.model.MtUser;
import com.bahar.repository.model.MtUserCoupon;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券核销 controller（收银端）
 *
 * 收银台「卡券核销 → 详情 / 确定核销」调的是 backendApi/doConfirm/info 与
 * backendApi/doConfirm/doConfirm，但后端原本没有这个控制器（只有
 * backendApi/userCoupon/doConfirm 与 clientApi/confirm/doConfirm），所以一直是 404。
 * 这里补齐，字段结构与 UserCouponServiceImpl 里组装 MyCouponDto 的口径保持一致。
 */
@Api(tags = "管理端-卡券核销相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/doConfirm")
public class BackendConfirmController extends BaseController {

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 核销日志服务接口
     * */
    private ConfirmLogService confirmLogService;

    /**
     * 会员卡券 mapper（按核销码查询用）
     * */
    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 查询核销信息（收银台「详情」与核销前回填用）
     */
    @ApiOperation(value = "查询核销信息")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:index')")
    public ResponseObject info(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String code = param.get("code") == null ? "" : param.get("code").toString();
        Integer id = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());

        MtUserCoupon userCoupon = null;
        if (id > 0) {
            userCoupon = userCouponService.getUserCouponDetail(id);
        }
        if (userCoupon == null && StringUtil.isNotEmpty(code)) {
            userCoupon = mtUserCouponMapper.findByCode(code);
        }
        if (userCoupon == null) {
            return getFailureResult(1003, "该卡券不存在！");
        }

        MtCoupon coupon = couponService.queryCouponById(userCoupon.getCouponId());
        if (coupon == null) {
            return getFailureResult(1003, "该卡券不存在！");
        }

        // 已核销次数
        int confirmCount = 0;
        Long confirmNum = confirmLogService.getConfirmNum(userCoupon.getId());
        if (confirmNum != null) {
            confirmCount = confirmNum.intValue();
        }

        Map<String, Object> couponInfo = new HashMap<>();
        couponInfo.put("id", userCoupon.getId());
        couponInfo.put("code", userCoupon.getCode());
        couponInfo.put("couponId", coupon.getId());
        couponInfo.put("type", coupon.getType());
        couponInfo.put("name", coupon.getName());
        couponInfo.put("content", coupon.getContent());
        couponInfo.put("description", coupon.getDescription());
        couponInfo.put("status", userCoupon.getStatus());
        couponInfo.put("amount", userCoupon.getAmount());
        couponInfo.put("balance", userCoupon.getBalance());
        // 计次卡的总次数取 outRule
        couponInfo.put("useRule", coupon.getOutRule());
        couponInfo.put("confirmCount", confirmCount);

        // 计次卡：面额显示总次数，余额显示已核销次数（与会员端 MyCouponDto 口径一致）
        if (coupon.getType() != null && coupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            if (StringUtil.isNotEmpty(coupon.getOutRule())) {
                couponInfo.put("amount", new BigDecimal(coupon.getOutRule()));
            }
            couponInfo.put("balance", new BigDecimal(confirmCount));
        }

        // 有效期：固定期限取券的开始/结束时间，灵活期限取领取/过期时间
        String effectiveDate = "";
        if (coupon.getExpireType() != null && coupon.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
            effectiveDate = DateUtil.formatDate(coupon.getBeginTime(), "yyyy.MM.dd HH:mm")
                    + "-" + DateUtil.formatDate(coupon.getEndTime(), "yyyy.MM.dd HH:mm");
        }
        if (coupon.getExpireType() != null && coupon.getExpireType().equals(CouponExpireTypeEnum.FLEX.getKey())) {
            effectiveDate = DateUtil.formatDate(userCoupon.getCreateTime(), "yyyy.MM.dd HH:mm")
                    + "-" + DateUtil.formatDate(userCoupon.getExpireTime(), "yyyy.MM.dd HH:mm");
        }
        couponInfo.put("effectiveDate", effectiveDate);

        MtUser userInfo = memberService.queryMemberById(userCoupon.getUserId());
        List<ParamDto> typeList = CouponTypeEnum.getCouponTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("couponInfo", couponInfo);
        result.put("userInfo", userInfo);
        result.put("typeList", typeList);
        return getSuccessResult(result);
    }

    /**
     * 执行核销
     */
    @ApiOperation(value = "核销卡券")
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:index')")
    public ResponseObject doConfirm(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String code = param.get("code") == null ? "" : param.get("code").toString();
        Integer userCouponId = param.get("userCouponId") == null ? 0 : Integer.parseInt(param.get("userCouponId").toString());
        String amount = (param.get("amount") == null || param.get("amount").toString().length() < 1) ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtUserCoupon userCoupon = null;
        if (userCouponId > 0) {
            userCoupon = userCouponService.getUserCouponDetail(userCouponId);
        }
        if (userCoupon == null && StringUtil.isNotEmpty(code)) {
            userCoupon = mtUserCouponMapper.findByCode(code);
        }
        if (userCoupon == null) {
            return getFailureResult(1003, "该卡券不存在！");
        }
        if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            return getFailureResult(1003, "该卡券状态异常！");
        }
        if (couponService.codeExpired(userCoupon.getCode())) {
            return getFailureResult(1003, "二维码已过期，请重新获取！");
        }

        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        MtCoupon coupon = couponService.queryCouponById(userCoupon.getCouponId());
        BigDecimal confirmAmount = userCoupon.getAmount();
        if (coupon != null && coupon.getType() != null && coupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            confirmAmount = userCoupon.getBalance();
        }
        if (new BigDecimal(amount).compareTo(new BigDecimal("0")) > 0) {
            confirmAmount = new BigDecimal(amount);
        }

        couponService.useCoupon(userCoupon.getId(), accountInfo.getId(), storeId, 0, confirmAmount,
                StringUtil.isEmpty(remark) ? "收银台核销" : remark);
        return getSuccessResult(true);
    }
}
