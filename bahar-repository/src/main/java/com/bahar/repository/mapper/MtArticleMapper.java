package com.bahar.repository.mapper;

import com.bahar.repository.model.MtArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtArticleMapper extends BaseMapper<MtArticle> {
   void increaseClick(@Param("articleId") Integer articleId);
}
