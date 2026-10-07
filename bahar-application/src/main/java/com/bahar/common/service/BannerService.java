package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.content.BannerDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.BannerPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtBanner;

import java.util.List;
import java.util.Map;

/**
 * 焦点图业务接口
 *
 * CopyRight https://www.bahar.cn
 */
public interface BannerService extends IService<MtBanner> {

    /**
     * 分页查询列表
     *
     * @param bannerPage
     * @return
     */
    PaginationResponse<MtBanner> queryBannerListByPagination(BannerPage bannerPage);

    /**
     * 添加Banner
     *
     * @param reqBannerDto
     * @throws BusinessCheckException
     * @return
     */
    MtBanner addBanner(BannerDto reqBannerDto) throws BusinessCheckException;

    /**
     * 根据ID获取Banner信息
     *
     * @param id Banner ID
     * @return
     */
    MtBanner queryBannerById(Integer id);

    /**
     * 更新焦点图
     *
     * @param  bannerDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * @return
     * */
    MtBanner updateBanner(BannerDto bannerDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据条件搜索焦点图
     *
     * @param params 查询参数
     * @return
     * */
    List<MtBanner> queryBannerListByParams(Map<String, Object> params);
}
