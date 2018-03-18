package com.crazypig.rpc.netty.protocol;

import java.io.Serializable;

/**
 * 封装RPC响应结果
 * @author CrazyPig
 *
 */
public class RpcResponse implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 返回状态码
     */
    private int code;
    /**
     * 封装请求结果
     */
    private Object result;
    
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    
}
