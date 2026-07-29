package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.merchant.MerchantSettingDto;
import com.bahar.common.dto.merchant.StaffDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.MerchantService;
import com.bahar.common.service.SettingService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.module.merchantApi.request.MerchantSettingParam;
import com.bahar.repository.model.MtUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商户相关controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-商户设置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/merchantSetting")
public class MerchantSettingController extends BaseController {

    /**
     * 会员服务接口
     * */
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
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 查询商户设置信息
     */
    @ApiOperation(value = "查询商户设置信息")
    @RequestMapping(value = "/settingInfo", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject settingInfo() {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        StaffDto staffInfo = staffService.getStaffInfoByMobile(mtUser.getMobile());
        if (null == staffInfo) {
            return getFailureResult(1002, "您的帐号不是商户，没有操作权限");
        }
        MerchantSettingDto merchantInfo = merchantService.getMerchantSettingInfo(staffInfo.getMerchantId(), staffInfo.getStoreId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("imagePath", settingService.getUploadBasePath());
        outParams.put("merchantInfo", merchantInfo);
        return getSuccessResult(outParams);
    }

    /**
     * 保存商户设置
     */
    @ApiOperation(value = "保存商户设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveSetting(@RequestBody MerchantSettingParam params) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        StaffDto staffInfo = staffService.getStaffInfoByMobile(mtUser.getMobile());
        if (null == staffInfo) {
            return getFailureResult(1002, "您的帐号不是商户，没有操作权限");
        }
        params.setMerchantId(staffInfo.getMerchantId());
        params.setStoreId(staffInfo.getStoreId());
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setMerchantId(staffInfo.getMerchantId());
        accountInfo.setAccountName(staffInfo.getRealName());
        MerchantSettingDto merchantInfo = merchantService.saveMerchantSetting(params, accountInfo);
        return getSuccessResult(merchantInfo);
    }
}
