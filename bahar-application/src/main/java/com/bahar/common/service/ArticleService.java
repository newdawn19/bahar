package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.content.ArticleDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.ArticlePage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtArticle;
/**
 * 文章业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface ArticleService extends IService<MtArticle> {

    /**
     * 分页查询文章列表
     *
     * @param articlePage
     * @return
     */
    PaginationResponse<ArticleDto> queryArticleListByPagination(ArticlePage articlePage);

    /**
     * 添加文章
     *
     * @param  articleDto
     * @throws BusinessCheckException
     */
    MtArticle addArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据ID获取文章信息
     *
     * @param  id 文章ID
     * @return
     */
    MtArticle queryArticleById(Integer id);

    /**
     * 根据ID获取文章详情
     *
     * @param  id 文章ID
     */
    ArticleDto getArticleDetail(Integer id);

    /**
     * 更新文章
     * @param  articleDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * @return
     * */
    MtArticle updateArticle(ArticleDto articleDto, AccountInfo accountInfo) throws BusinessCheckException;

}
