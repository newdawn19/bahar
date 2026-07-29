package com.bahar.module.clientApi.controller;

import com.bahar.common.Constants;
import com.bahar.common.dto.coupon.GiveDto;
import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.param.GiveListParam;
import com.bahar.common.param.GivePage;
import com.bahar.common.param.GiveParam;
import com.bahar.common.service.GiveService;
import com.bahar.common.service.MemberService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 卡券转赠controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="会员端-卡券转赠相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/give")
public class ClientGiveController extends BaseController {

    /**
     * 转赠服务接口
     */
    private GiveService giveService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 转赠卡券
     */
    @ApiOperation(value = "转赠卡券")
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(@RequestBody GiveParam giveParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        giveParam.setUserId(mtUser.getId());
        giveParam.setStoreId(mtUser.getStoreId());
        giveParam.setMerchantId(mtUser.getMerchantId());
        ResponseObject result = giveService.addGive(giveParam);
        return getSuccessResult(result.getData());
    }

    /**
     * 查询转赠记录
     */
    @ApiOperation(value = "查询转赠记录")
    @RequestMapping(value = "/giveLog", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject giveLog(@RequestBody GiveListParam giveListParam) {
        UserInfo mtUser = TokenUtil.getUserInfo();
        String mobile = giveListParam.getMobile() == null ? "" : giveListParam.getMobile();
        String type = giveListParam.getType() == null ? "give" : giveListParam.getType();
        Integer page = giveListParam.getPage() == null ? Constants.PAGE_NUMBER : giveListParam.getPage();
        Integer pageSize = giveListParam.getPageSize() == null ? Constants.PAGE_SIZE : giveListParam.getPageSize();

        GivePage givePage = new GivePage();
        givePage.setPage(page);
        givePage.setPageSize(pageSize);
        if (type.equals("gived")) {
            givePage.setUserId(mtUser.getId());
        } else {
            givePage.setGiveUserId(mtUser.getId());
        }

        if (StringUtil.isNotEmpty(mobile) && type.equals("give")) {
            givePage.setMobile(mobile);
        } else if(StringUtil.isNotEmpty(mobile) && type.equals("gived")) {
            givePage.setUserMobile(mobile);
        }
        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(givePage);

        Map<String, Object> outParams = new HashMap();
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(outParams);
    }
}

