package com.rsh.coviewer.bean;

import com.rsh.coviewer.tool.Tool;

import java.io.Serializable;

/**
 * 电影的序列化对象
 * Created by rsh on 2018/7/2.
 */
public class MovieBean implements Serializable {
    private String reason;
    private int error_code;
    private ResultBean result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = Tool.getInstance().isNullOrEmpty(error_code)?-1:error_code;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }
}
