package com.bahar.module.backendApi.controller.member;
import com.bahar.common.dto.order.OrderDto;
import com.bahar.common.enums.OrderStatusEnum;
import com.bahar.common.enums.OrderTypeEnum;
import com.bahar.common.enums.PayStatusEnum;
import com.bahar.common.service.OrderService;
import com.bahar.repository.model.MtOrder;

import com.bahar.common.dto.recharge.RechargeRuleDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.member.BalanceDto;
import com.bahar.common.enums.BalanceSettingEnum;
import com.bahar.common.enums.SettingTypeEnum;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.BalancePage;
import com.bahar.common.service.BalanceService;
import com.bahar.common.service.CouponService;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.SettingService;
import com.bahar.common.util.CommonUtil;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtBalance;
import com.bahar.repository.model.MtCoupon;
import com.bahar.repository.model.MtSetting;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 余额管理controller
 *
 * CopyRight https://www.bahar.cn
 */
@Slf4j
@Api(tags="管理端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/balance")
public class BackendBalanceController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 余额明细列表查询
     */
    @ApiOperation(value = "余额明细列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:list')")
    public ResponseObject list(@ModelAttribute BalancePage balancePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            balancePage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            balancePage.setMerchantId(accountInfo.getMerchantId());
        }

        PaginationResponse<BalanceDto> paginationResponse = balanceService.queryBalanceListByPagination(balancePage);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        return getSuccessResult(result);
    }

    /**
     * 提交充值（单个会员）
     */
    @ApiOperation(value = "提交充值")
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:modify')")
    public ResponseObject doRecharge(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String amount = param.get("amount") == null ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "后台充值" : param.get("remark").toString();
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        Integer type = param.get("type") == null ? 1 : Integer.parseInt(param.get("type").toString());// 1 增加，2 扣减
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (!CommonUtil.isNumeric(amount)) {
            return getFailureResult(201, "充值金额必须是数字");
        }
        if (userId < 1) {
            return getFailureResult(201, "充值会员信息不能为空");
        }

        MtBalance mtBalance = new MtBalance();
        MtUser userInfo = memberService.queryMemberById(userId);
        // 平台方/未绑定商户的账号(merchantId<=0，收银端演示账号就是这种)没有可比较的商户，
        // 改以会员所属商户为准，口径与 BackendCashierController.resolveStore() 的兜底一致
        Integer merchantId = accountInfo.getMerchantId();
        log.info("token account info:{} user info:{}", merchantId, userInfo.getMerchantId());
        if (merchantId == null || merchantId <= 0) {
            merchantId = userInfo.getMerchantId();
        } else if (!merchantId.equals(userInfo.getMerchantId())) {
            return getFailureResult(201, "不同商户，无充值权限");
        }
        // 赠送金额：收银端「确定充值」会把 giftAmount 一起传过来，后端原本没读这个字段，
        // 界面显示「充1000送50」但会员只到账 1000，赠送被静默丢掉。
        BigDecimal giftAmount = new BigDecimal("0");
        Object giftObj = param.get("giftAmount");
        if (type == 1 && giftObj != null && CommonUtil.isNumeric(giftObj.toString())) {
            giftAmount = new BigDecimal(giftObj.toString());
        }

        // 扣减余额
        if (type == 2) {
            if (userInfo.getBalance().compareTo(new BigDecimal(amount)) < 0) {
                return getFailureResult(201, "操作失败，会员余额不足");
            }
            mtBalance.setAmount(new BigDecimal(amount).subtract(new BigDecimal(amount).multiply(new BigDecimal("2"))));
        } else {
            mtBalance.setAmount(new BigDecimal(amount));
        }
        mtBalance.setMerchantId(merchantId);
        mtBalance.setStoreId(accountInfo.getStoreId());
        mtBalance.setDescription(remark);
        mtBalance.setUserId(userId);
        mtBalance.setOperator(accountInfo.getAccountName());

        balanceService.addBalance(mtBalance, true);

        // 赠送金额单独记一条，余额明细里能看出本金/赠送各多少
        if (giftAmount.compareTo(BigDecimal.ZERO) > 0) {
            MtBalance giftBalance = new MtBalance();
            giftBalance.setMerchantId(merchantId);
            giftBalance.setStoreId(accountInfo.getStoreId());
            giftBalance.setAmount(giftAmount);
            giftBalance.setDescription("充值赠送");
            giftBalance.setUserId(userId);
            giftBalance.setOperator(accountInfo.getAccountName());
            balanceService.addBalance(giftBalance, true);
        }
        return getSuccessResult(true);
    }

    /**
     * 生成充值订单（收银端扫码收款链路的第一步）
     *
     * 前端流程：createRechargeOrder -> clientApi/pay/doPay?orderId=&authCode=&payType=MICROPAY
     * 支付成功后 paymentCallback 会自动给会员入账并加积分。
     *
     * 注意：不复用 OrderService.doRecharge(request, param)，因为它是会员端接口，
     * 依赖 request 头里的 merchantNo；收银端没有该头，
     * merchantService.getMerchantId("") 会返回 0，导致订单 merchantId=0。
     */
    @ApiOperation(value = "生成充值订单")
    @RequestMapping(value = "/createRechargeOrder", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:modify')")
    public ResponseObject createRechargeOrder(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String amount = param.get("amount") == null ? "0" : param.get("amount").toString();
        String customAmount = param.get("customAmount") == null ? "0" : param.get("customAmount").toString();
        String remark = param.get("remark") == null ? "会员充值" : param.get("remark").toString();
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (!CommonUtil.isNumeric(amount)) {
            return getFailureResult(201, "充值金额必须是数字");
        }
        if (userId < 1) {
            return getFailureResult(201, "充值会员信息不能为空");
        }

        BigDecimal rechargeAmount = new BigDecimal(amount);
        if (rechargeAmount.compareTo(new BigDecimal("0")) <= 0) {
            return getFailureResult(201, "请确认充值金额");
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (userInfo == null) {
            return getFailureResult(201, "会员不存在");
        }
        // 平台方/未绑定商户的账号(merchantId<=0，收银端演示账号就是这种)没有可比较的商户，
        // 改以会员所属商户为准，口径与 BackendCashierController.resolveStore() 的兜底一致
        Integer merchantId = accountInfo.getMerchantId();
        if (merchantId == null || merchantId <= 0) {
            merchantId = userInfo.getMerchantId();
        } else if (!merchantId.equals(userInfo.getMerchantId())) {
            return getFailureResult(201, "不同商户，无充值权限");
        }

        // 充值赠送规则，与会员端充值一致，取自 mt_setting 的 RECHARGE_RULE
        String ruleParam = rechargeAmount.toPlainString() + "_0";
        BigDecimal custom = new BigDecimal(CommonUtil.isNumeric(customAmount) ? customAmount : "0");
        if (custom.compareTo(new BigDecimal("0")) > 0) {
            // 自定义金额不参与赠送
            rechargeAmount = custom;
            ruleParam = custom.toPlainString() + "_0";
        } else {
            MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.BALANCE.getKey(), BalanceSettingEnum.RECHARGE_RULE.getKey());
            if (mtSetting != null && mtSetting.getStatus() != null
                    && mtSetting.getStatus().equals(StatusEnum.ENABLED.getKey())
                    && StringUtil.isNotEmpty(mtSetting.getValue())) {
                String rules[] = mtSetting.getValue().split(",");
                for (String rule : rules) {
                    String amountArr[] = rule.split("_");
                    if (amountArr.length >= 2) {
                        try {
                            if (new BigDecimal(amountArr[0]).compareTo(rechargeAmount) == 0) {
                                ruleParam = rule;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            // 规则配置非法，跳过
                        }
                    }
                }
            }
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setType(OrderTypeEnum.RECHARGE.getKey());
        orderDto.setUserId(userId);
        orderDto.setStoreId(accountInfo.getStoreId());
        orderDto.setAmount(rechargeAmount);
        // 必须同时设置 payAmount：微信/支付宝回调会拿回调金额与 payAmount 做等值比对，
        // 只设 amount 会让 pay_amount 落库为 0，回调时金额校验失败 -> 用户付了钱但不入账。
        orderDto.setPayAmount(rechargeAmount);
        orderDto.setUsePoint(0);
        orderDto.setRemark(remark);
        orderDto.setParam(ruleParam);
        orderDto.setStatus(OrderStatusEnum.CREATED.getKey());
        orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setOrderMode("");
        orderDto.setCouponId(0);
        orderDto.setPlatform("cashier");
        orderDto.setMerchantId(merchantId);

        MtOrder orderInfo = orderService.saveOrder(orderDto);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderInfo.getId());
        result.put("orderSn", orderInfo.getOrderSn());
        result.put("payAmount", orderInfo.getAmount());
        return getSuccessResult(result);
    }

    /**
     * 发放余额
     */
    @ApiOperation(value = "发放余额")
    @RequestMapping(value = "/distribute", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:distribute')")
    public ResponseObject distribute(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String amount = param.get("amount") == null ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "后台充值" : param.get("remark").toString();
        String userIds = param.get("userIds") == null ? "" : param.get("userIds").toString();
        String object = param.get("object") == null ? "" : param.get("object").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        balanceService.distribute(accountInfo, object, userIds, amount, remark);
        return getSuccessResult(true);
    }

    /**
     * 充值设置详情
     */
    @ApiOperation(value = "充值设置详情")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:setting')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.BALANCE.getKey());

        List<RechargeRuleDto> rechargeRuleList = new ArrayList<>();
        String remark = "";
        String status = "";
        if (settingList.size() > 0) {
            for (MtSetting setting : settingList) {
                 if (setting.getName().equals(BalanceSettingEnum.RECHARGE_RULE.getKey())) {
                     status = setting.getStatus();
                     String item[] = setting.getValue().split(",");
                     if (item.length > 0) {
                         for (String value : item) {
                              String el[] = value.split("_");
                              if (el.length >= 2) {
                                  RechargeRuleDto ruleDto = new RechargeRuleDto();
                                  ruleDto.setRechargeAmount(el[0]);
                                  ruleDto.setGiveAmount(el[1]);
                                  if (el.length >= 3) {
                                      ruleDto.setGiveCouponIds(el[2]);
                                  }
                                  rechargeRuleList.add(ruleDto);
                              }
                         }
                     }
                 } else if(setting.getName().equals(BalanceSettingEnum.RECHARGE_REMARK.getKey())) {
                     remark = setting.getValue();
                 }
            }
        }

        Map<String, Object> result = new HashMap();
        result.put("rechargeRuleList", rechargeRuleList);
        result.put("remark", remark);
        result.put("status", status);

        return getSuccessResult(result);
    }

    /**
     * 保存充值设置
     */
    @ApiOperation(value = "保存充值设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('balance:setting')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        List<LinkedHashMap> rechargeItems = (List) param.get("rechargeItem");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }
        if (rechargeItems.size() < 0) {
            return getFailureResult(201, "充值规则设置不能为空");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        String rechargeRule = "";
        List<String> amounts = new ArrayList<>();
        for (LinkedHashMap item : rechargeItems) {
             String amount = item.get("rechargeAmount").toString();
             String giveCouponIds = item.get("giveCouponIds") == null ? "" : item.get("giveCouponIds").toString();
             if (StringUtil.isNotBlank(giveCouponIds)) {
                 String[] couponIds = giveCouponIds.split("\\|");
                 for (int i = 0; i < couponIds.length; i++) {
                      MtCoupon mtCoupon = couponService.queryCouponById(Integer.parseInt(couponIds[i]));
                      if (mtCoupon == null) {
                          return getFailureResult(201, "赠送卡券ID:"+couponIds[i]+"不存在，请核实！");
                      }
                 }
             }
             if (amounts.contains(amount)) {
                 return getFailureResult(201, "充值金额设置不能有重复");
             }
             if (rechargeRule.length() == 0) {
                 rechargeRule = item.get("rechargeAmount").toString() + '_' + item.get("giveAmount").toString();
             } else {
                 rechargeRule = rechargeRule + ',' + item.get("rechargeAmount").toString() + '_' + item.get("giveAmount").toString();
             }
             if (StringUtil.isNotBlank(giveCouponIds)) {
                 rechargeRule = rechargeRule + '_' + giveCouponIds;
             }
             amounts.add(amount);
        }

        MtSetting setting = new MtSetting();
        setting.setMerchantId(accountInfo.getMerchantId());
        setting.setType(SettingTypeEnum.BALANCE.getKey());
        setting.setName(BalanceSettingEnum.RECHARGE_RULE.getKey());
        setting.setValue(rechargeRule);
        setting.setDescription(BalanceSettingEnum.RECHARGE_RULE.getValue());
        setting.setStatus(status);
        setting.setOperator(accountInfo.getAccountName());
        setting.setUpdateTime(new Date());
        settingService.saveSetting(setting);

        // 保存充值说明
        MtSetting settingRemark = new MtSetting();
        settingRemark.setMerchantId(accountInfo.getMerchantId());
        settingRemark.setType(SettingTypeEnum.BALANCE.getKey());
        settingRemark.setName(BalanceSettingEnum.RECHARGE_REMARK.getKey());
        settingRemark.setValue(remark);
        settingRemark.setDescription("");
        settingRemark.setStatus(status);
        settingRemark.setOperator(accountInfo.getAccountName());
        settingRemark.setUpdateTime(new Date());
        settingService.saveSetting(settingRemark);

        return getSuccessResult(true);
    }
}
