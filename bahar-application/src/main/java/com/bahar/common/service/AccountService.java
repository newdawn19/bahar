package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.system.AccountDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.AccountPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.module.backendApi.request.LoginRequest;
import com.bahar.module.backendApi.response.LoginResponse;
import com.bahar.repository.model.TAccount;
import com.bahar.repository.model.TDuty;

import java.util.List;

/**
 * 后台账号接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface AccountService extends IService<TAccount> {

    /**
     * 分页查询账号列表
     *
     * @param accountPage
     * @return
     */
    PaginationResponse<AccountDto> getAccountListByPagination(AccountPage accountPage);

    /**
     * 根据账号名称获取账号信息
     *
     * @param userName 账号名称
     * @return
     * */
    AccountInfo getAccountByName(String userName);

    /**
     * 获取用户信息
     *
     * @param id 账号ID
     * @return
     */
    TAccount getAccountInfoById(Integer id);

    /**
     * 创建账号信息
     *
     * @param accountInfo 账号信息
     * @param duties 角色
     * @return
     * */
    TAccount createAccountInfo(TAccount accountInfo, List<TDuty> duties) throws BusinessCheckException;

    /**
     * 获取账号角色ID
     *
     * @param accountId 账号ID
     * @return
     * */
    List<Long> getRoleIdsByAccountId(Integer accountId);

    /**
     * 修改账户
     *
     * @param tAccount 账户实体
     * @throws BusinessCheckException
     * @return
     */
    void editAccount(TAccount tAccount, List<TDuty> duties) throws BusinessCheckException;

    /**
     * 根据账户名称获取账户所分配的角色ID集合
     *
     * @param accountId 账户
     * @return 角色ID集合
     */
    List<Integer> getDutyIdsByAccountId(Integer accountId);

    /**
     * 更新账户信息
     *
     * @param tAccount
     * @throws BusinessCheckException
     * @return
     */
    void updateAccount(TAccount tAccount, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 删除后台账号
     *
     * @param accountId 账号ID
     * @return
     * */
    void deleteAccount(Long accountId, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 密码加密
     *
     * @param tAccount 账号信息
     * @return
     * */
    void entryptPassword(TAccount tAccount);

    /**
     * 获取加密密码
     *
     * @param password
     * @param salt
     * @return
     * */
    String getEntryptPassword(String password, String salt);

    /**
     * 登录后台系统
     *
     * @param loginRequest 登录参数
     * @param userAgent 登录浏览器
     * @return
     * */
    LoginResponse doLogin(LoginRequest loginRequest, String userAgent) throws BusinessCheckException;
}
