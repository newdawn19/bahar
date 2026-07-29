package com.bahar.module.backendApi.controller.message;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.SettingTypeEnum;
import com.bahar.common.enums.SmsSettingEnum;
import com.bahar.common.param.SmsPage;
import com.bahar.common.service.SendSmsService;
import com.bahar.common.service.SettingService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtSetting;
import com.bahar.repository.model.MtSmsSendedLog;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-短信相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/smsManager")
public class BackendSmsController extends BaseController {

    /**
     * 短信发送接口
     */
    private SendSmsService sendSmsService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 查询已发短信列表
     */
    @ApiOperation(value = "查询已发短信列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(@ModelAttribute SmsPage smsPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            smsPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            smsPage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<MtSmsSendedLog> paginationResponse = sendSmsService.querySmsListByPagination(smsPage);
        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 获取短信设置
     */
    @ApiOperation(value = "获取短信设置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.SMS_CONFIG.getKey());

        String isClose = "0";
        String accessKeyId = "";
        String accessKeySecret = "";
        String signName = "";
        for (MtSetting setting : settingList) {
            if (StringUtil.isNotEmpty(setting.getValue())) {
                if (setting.getName().equals(SmsSettingEnum.IS_CLOSE.getKey())) {
                    isClose = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.ACCESS_KEY_ID.getKey())) {
                    accessKeyId = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.ACCESS_KEY_SECRET.getKey())) {
                    accessKeySecret = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.SIGN_NAME.getKey())) {
                    signName = setting.getValue();
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isClose", isClose);
        result.put("accessKeyId", accessKeyId);
        result.put("accessKeySecret", accessKeySecret);
        result.put("signName", signName);

        return getSuccessResult(result);
    }

    /**
     * 保存短信设置
     */
    @ApiOperation(value = "保存短信设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String isClose = param.get("isClose") != null ? param.get("isClose").toString() : null;
        String accessKeyId = param.get("accessKeyId") != null ? param.get("accessKeyId").toString() : null;
        String accessKeySecret = param.get("accessKeySecret") != null ? param.get("accessKeySecret").toString() : null;
        String signName = param.get("signName") != null ? param.get("signName").toString() : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            return getFailureResult(1004);
        }
        SmsSettingEnum[] settingList = SmsSettingEnum.values();
        for (SmsSettingEnum setting : settingList) {
            MtSetting mtSetting = new MtSetting();
            mtSetting.setType(SettingTypeEnum.SMS_CONFIG.getKey());
            mtSetting.setName(setting.getKey());
            if (setting.getKey().equals(SmsSettingEnum.IS_CLOSE.getKey())) {
                mtSetting.setValue(isClose);
            } else if (setting.getKey().equals(SmsSettingEnum.ACCESS_KEY_ID.getKey())) {
                mtSetting.setValue(accessKeyId);
            } else if (setting.getKey().equals(SmsSettingEnum.ACCESS_KEY_SECRET.getKey())) {
                mtSetting.setValue(accessKeySecret);
            } else if (setting.getKey().equals(SmsSettingEnum.SIGN_NAME.getKey())) {
                mtSetting.setValue(signName);
            }
            mtSetting.setDescription(setting.getValue());
            mtSetting.setOperator(accountInfo.getAccountName());
            mtSetting.setUpdateTime(new Date());
            mtSetting.setMerchantId(accountInfo.getMerchantId());
            mtSetting.setStoreId(0);
            settingService.saveSetting(mtSetting);
        }

        return getSuccessResult(true);
    }

}
