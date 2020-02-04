package com.ncov.stats.result;

import com.ncov.stats.global.Response;
import lombok.Data;

@Data
public class CommonResult {
    private String msghd_rspcode;

    private String msghd_rspmsg;

    private Object result;

    public void success(Object resp) {
        this.msghd_rspcode = Response.SUCCEED;
        this.msghd_rspmsg = "成功";
        this.result= resp;
    }

    public void fail(String error_msg) {
        this.msghd_rspcode = Response.ERROR;
        this.msghd_rspmsg = error_msg;
    }

    public void fail() {
        this.msghd_rspcode = Response.ERROR;
        this.msghd_rspmsg = "失败";
    }

    public String toString() {
        return "CommonResult [code=" + this.msghd_rspcode + ", msg=" + this.msghd_rspmsg + "]";
    }
}
