package com.bahar.common.vo.printer;

/**
 * 订单状态
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public enum OrderStatusType {

    /**
     * 完成
     */
    Completed("A"),

    /**
     * 失败
     */
    Failed("B");

    private final String val;

    public String getVal() {
        return val;
    }

    OrderStatusType(String type) {
        this.val = type;
    }

}
