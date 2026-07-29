package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.param.ActionLogPage;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.TActionLog;

/**
 * 后台日志服务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface ActionLogService extends IService<TActionLog> {

    /**
     * 保存日志
     *
     * @param actionLog
     * @return
     */
    void saveActionLog(TActionLog actionLog);

    /**
     * 获取分页查询数据
     *
     * @param actionLogPage
     * @return
     */
    PaginationResponse<TActionLog> findLogsByPagination(ActionLogPage actionLogPage);
}
