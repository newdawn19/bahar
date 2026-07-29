package com.bahar.module.backendApi.controller.content;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.BannerPage;
import com.bahar.common.param.StatusParam;
import com.bahar.common.service.StoreService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.common.dto.content.BannerDto;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.service.SettingService;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.common.service.BannerService;
import com.bahar.repository.model.MtBanner;
import com.bahar.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 焦点图管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags = "管理端-焦点图相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/banner")
public class BackendBannerController extends BaseController {

    /**
     * 焦点图服务接口
     */
    private BannerService bannerService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 焦点图列表查询
     */
    @ApiOperation(value = "焦点图列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:list')")
    public ResponseObject list(@ModelAttribute BannerPage bannerPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bannerPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bannerPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtBanner> paginationResponse = bannerService.queryBannerListByPagination(bannerPage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新焦点图状态
     */
    @ApiOperation(value = "更新焦点图状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:edit')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        String status = params.getStatus() != null ? params.getStatus() : StatusEnum.ENABLED.getKey();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        BannerDto bannerDto = new BannerDto();
        bannerDto.setOperator(accountInfo.getAccountName());
        bannerDto.setId(params.getId());
        bannerDto.setStatus(status);
        bannerService.updateBanner(bannerDto, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 保存焦点图
     */
    @ApiOperation(value = "保存焦点图")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:add')")
    public ResponseObject saveHandler(@RequestBody BannerDto bannerDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        bannerDto.setOperator(accountInfo.getAccountName());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bannerDto.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bannerDto.setStoreId(accountInfo.getStoreId());
        }
        if (bannerDto.getId() != null && bannerDto.getId() > 0) {
            bannerService.updateBanner(bannerDto, accountInfo);
        } else {
            bannerService.addBanner(bannerDto);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取焦点图详情
     */
    @ApiOperation(value = "获取焦点图详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:list')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBanner bannerInfo = bannerService.queryBannerById(id);

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!bannerInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bannerInfo", bannerInfo);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }
}
