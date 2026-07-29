package com.bahar.common.vo.printer;

/**
 * 打印机请求参数
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public class PrinterRequest extends RestRequest {

    /**
     * 打印机编号
     */
    private String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
