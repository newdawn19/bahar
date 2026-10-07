package com.bahar.module.backendApi.controller.common;

import com.bahar.common.Constants;
import com.bahar.common.domain.TreeNode;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.AdminRoleEnum;
import com.bahar.common.service.AccountService;
import com.bahar.common.service.DutyService;
import com.bahar.common.service.SourceService;
import com.bahar.common.util.TokenUtil;
import com.bahar.common.util.TreeUtil;
import com.bahar.common.vo.RouterVo;
import com.bahar.framework.annoation.OperationServiceLog;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.module.backendApi.request.LoginRequest;
import com.bahar.module.backendApi.response.LoginResponse;
import com.bahar.repository.model.TAccount;
import com.bahar.repository.model.TDuty;
import com.bahar.repository.model.TSource;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台登录接口
 *
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-后台登录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/backendApi/login")
public class BackendLoginController extends BaseController {

    /**
     * 后台账号服务接口
     * */
    private AccountService accountService;

    /**
     * 后台菜单服务接口
     * */
    private SourceService sourceService;

    /**
     * 后台角色服务接口
     * */
    private DutyService dutyService;

    /**
     * 后台登录
     * */
    @ApiOperation(value = "后台登录")
    @RequestMapping(value="/doLogin", method = RequestMethod.POST)
    public ResponseObject doLogin(HttpServletRequest request, @RequestBody LoginRequest loginRequest) throws BusinessCheckException {
        LoginResponse response = accountService.doLogin(loginRequest, request.getHeader("user-agent"));
        return getSuccessResult(response);
    }

    /**
     * 获取登录信息接口
     * */
    @ApiOperation(value = "获取登录信息")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public ResponseObject getInfo() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo == null) {
            return getFailureResult(401, "登录信息已失效，请重新登录");
        }
        TAccount tAccount = accountService.getAccountInfoById(accountInfo.getId());
        if (accountInfo == null || tAccount == null || !tAccount.getAccountStatus().toString().equals("1")) {
            return getFailureResult(Constants.HTTP_RESPONSE_CODE_NOLOGIN);
        }

        List<Long> roleIds = accountService.getRoleIdsByAccountId(accountInfo.getId());
        List<String> roles = new ArrayList<>();
        if (roleIds.size() > 0) {
            for (int i = 0; i < roleIds.size(); i++) {
                 TDuty role = dutyService.getRoleById(roleIds.get(i));
                 for (AdminRoleEnum item : AdminRoleEnum.values()) {
                      if (role.getDutyType().equals(item.getKey())) {
                          roles.add(item.getValue());
                      }
                 }
            }
        }

        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getMerchantId(), accountInfo.getId());
        List<String> permissions = new ArrayList<>();
        if (sources.size() > 0) {
            for (TSource source : sources) {
                if (source.getPath() != null) {
                    String permission = source.getPath().replaceAll("/", ":");
                    permissions.add(permission);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("accountInfo", accountInfo);
        result.put("roles", roles);
        result.put("permissions", permissions);

        return getSuccessResult(result);
    }

    /**
     * 获取登录路由菜单接口
     */
    @ApiOperation(value = "获取登录路由菜单接口")
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getRouters() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo == null) {
            return getFailureResult(401, "登录信息已失效，请重新登录");
        }

        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getMerchantId(), accountInfo.getId());

        List<TreeNode> trees = new ArrayList<>();
        TreeNode treeNode;
        for (TSource tSource : sources) {
            treeNode = new TreeNode();
            treeNode.setName(tSource.getSourceName());
            treeNode.setEname(tSource.getEname());
            treeNode.setNewIcon(tSource.getNewIcon());
            treeNode.setPath(tSource.getPath());
            treeNode.setId(tSource.getSourceId());
            treeNode.setLevel(tSource.getSourceLevel());
            treeNode.setIsMenu(tSource.getIsMenu());
            treeNode.setSort((tSource.getSourceStyle() == null || StringUtil.isEmpty(tSource.getSourceStyle())) ? 0 : Integer.parseInt(tSource.getSourceStyle()));
            if (tSource.getParentId() != null) {
                treeNode.setPId(tSource.getParentId());
            }
            treeNode.setUrl(tSource.getSourceCode());
            treeNode.setIcon(tSource.getIcon());
            trees.add(treeNode);
        }

        List<TreeNode> treeNodes = TreeUtil.sourceTreeNodes(trees);
        List<RouterVo> routers = sourceService.buildMenus(treeNodes);

        return getSuccessResult(routers);
    }

    /**
     * 退出后台登录
     * */
    @ApiOperation(value = "退出后台登录")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @OperationServiceLog(description = "退出后台系统")
    public ResponseObject logout(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(Constants.HTTP_RESPONSE_CODE_USER_NOT_EXIST);
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo != null) {
            TokenUtil.removeToken(token);
        }

        return getSuccessResult(true);
    }
}
