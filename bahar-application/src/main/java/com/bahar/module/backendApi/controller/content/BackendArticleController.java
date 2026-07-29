package com.bahar.module.backendApi.controller.content;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.content.ArticleDto;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.ArticlePage;
import com.bahar.common.param.StatusParam;
import com.bahar.common.service.ArticleService;
import com.bahar.common.service.SettingService;
import com.bahar.common.service.StoreService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtArticle;
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
 * 文章管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-文章相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/article")
public class BackendArticleController extends BaseController {

    /**
     * 文章服务接口
     */
    private ArticleService articleService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 文章列表查询
     */
    @ApiOperation(value = "文章列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:index')")
    public ResponseObject list(@ModelAttribute ArticlePage articlePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            articlePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            articlePage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(articlePage);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新文章状态
     */
    @ApiOperation(value = "更新文章状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:edit')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String status = params.getStatus() != null ? params.getStatus() : StatusEnum.ENABLED.getKey();

        MtArticle mtArticle = articleService.queryArticleById(params.getId());
        if (mtArticle == null) {
            return getFailureResult(201);
        }

        ArticleDto article = new ArticleDto();
        article.setOperator(accountInfo.getAccountName());
        article.setId(params.getId());
        article.setStatus(status);
        articleService.updateArticle(article, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 保存文章
     */
    @ApiOperation(value = "保存文章")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:add')")
    public ResponseObject saveHandler(@RequestBody ArticleDto articleDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        articleDto.setOperator(accountInfo.getAccountName());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            articleDto.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            articleDto.setStoreId(accountInfo.getStoreId());
        }

        if (articleDto.getId() != null && articleDto.getId() > 0) {
            articleService.updateArticle(articleDto, accountInfo);
        } else {
            articleService.addArticle(articleDto);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取文章详情
     */
    @ApiOperation(value = "获取文章详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:article:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtArticle articleInfo = articleService.queryArticleById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!articleInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("articleInfo", articleInfo);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }
}
