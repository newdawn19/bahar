package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.coupon.GiveDto;
import com.bahar.common.param.GivePage;
import com.bahar.common.param.GiveParam;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtGive;
import com.bahar.repository.model.MtGiveItem;

import java.util.List;
import java.util.Map;

/**
 * 转赠业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface GiveService extends IService<MtGive> {

    /**
     * 分页查询列表
     *
     * @param givePage
     * @return
     */
    PaginationResponse<GiveDto> queryGiveListByPagination(GivePage givePage);

    /**
     * 转赠卡券
     *
     * @param giveParam
     * @throws BusinessCheckException
     * @return
     */
    ResponseObject addGive(GiveParam giveParam) throws BusinessCheckException;

    /**
     * 根据ID获取信息
     *
     * @param id ID
     * @return
     */
    MtGive queryGiveById(Long id);

    /**
     * 根据条件搜索转赠详情
     *
     * @param params
     * @return
     * */
    List<MtGiveItem> queryItemByParams(Map<String, Object> params);
}
