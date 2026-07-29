package com.bahar.module.backendApi.controller.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bahar.common.dto.content.NavigationDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.SettingTypeEnum;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.service.SettingService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtSetting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 导航管理controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags = "管理端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/navigate")
public class BackendNavigateController extends BaseController {

    /**
     * 配置服务接口
     */
    private SettingService settingService;

    /**
     * 导航设置详情
     */
    @ApiOperation(value = "导航设置详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info() throws BusinessCheckException, JsonProcessingException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<NavigationDto> navigation = settingService.getNavigation(accountInfo.getMerchantId(), accountInfo.getStoreId(), null);
        Map<String, Object> result = new HashMap();
        result.put("navigation", navigation);
        result.put("imagePath", settingService.getUploadBasePath());
        return getSuccessResult(result);
    }

    /**
     * 保存导航设置
     */
    @ApiOperation(value = "保存导航设置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(@RequestBody NavigationDto navigation) throws BusinessCheckException, JsonProcessingException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        MtSetting mtSetting = new MtSetting();
        mtSetting.setMerchantId(accountInfo.getMerchantId());
        mtSetting.setStoreId(accountInfo.getStoreId());
        mtSetting.setType(SettingTypeEnum.NAVIGATION.getKey());
        mtSetting.setName(SettingTypeEnum.NAVIGATION.getKey());
        navigation.setStatus(navigation.getStatus() == null ? StatusEnum.ENABLED.getKey() : navigation.getStatus());
        if (navigation != null) {
            List<NavigationDto> navigationNew = new ArrayList<>();
            List<NavigationDto> navigations = settingService.getNavigation(accountInfo.getMerchantId(), accountInfo.getStoreId(), null);
            if (!navigation.getStatus().equals(StatusEnum.DISABLE.getKey())) {
                boolean exit = false;
                for (NavigationDto item : navigations) {
                    if (item.getName().equals(navigation.getName())) {
                        exit = true;
                    }
                }
                if (!exit) {
                    navigationNew.add(navigation);
                }
            }
            if (navigations != null && navigations.size() > 0) {
                for (NavigationDto item : navigations) {
                    if (!item.getName().equals(navigation.getName())) {
                        navigationNew.add(item);
                    } else {
                        if (!navigation.getStatus().equals(StatusEnum.DISABLE.getKey())) {
                            navigationNew.add(navigation);
                        }
                    }
                }
            }
            mtSetting.setValue(objectMapper.writeValueAsString(navigationNew));
        } else {
            mtSetting.setValue("");
        }
        mtSetting.setDescription(SettingTypeEnum.NAVIGATION.getValue());
        mtSetting.setStatus(StatusEnum.ENABLED.getKey());
        mtSetting.setOperator(accountInfo.getAccountName());
        Date dateTime = new Date();
        mtSetting.setUpdateTime(dateTime);
        mtSetting.setCreateTime(dateTime);

        settingService.saveSetting(mtSetting);
        return getSuccessResult(true);
    }
}
