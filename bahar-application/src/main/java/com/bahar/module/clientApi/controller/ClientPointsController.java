package com.bahar.module.clientApi.controller;

import com.bahar.common.dto.member.PointDto;
import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.GivePointParam;
import com.bahar.common.param.PointPage;
import com.bahar.common.service.PointService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 积分相关controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="会员端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/points")
public class ClientPointsController extends BaseController {

    /**
     * 积分服务接口
     */
    private PointService pointService;

    /**
     * 查询我的积分明细
     */
    @ApiOperation(value = "查询我的积分明细")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(@ModelAttribute PointPage pointPage) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        pointPage.setUserId(mtUser.getId());
        pointPage.setStatus(StatusEnum.ENABLED.getKey());
        PaginationResponse<PointDto> paginationResponse = pointService.queryPointListByPagination(pointPage);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 转赠积分
     */
    @ApiOperation(value = "转赠积分")
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(@RequestBody GivePointParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        String mobile = param.getMobile();
        String remark = param.getRemark();
        Integer amount = param.getAmount();

        boolean result = pointService.doGift(mtUser.getId(), mobile, amount, remark);
        if (result) {
            return getSuccessResult(true);
        } else {
            return getFailureResult(3008);
        }
    }
}
