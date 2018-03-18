package com.crazypig.rpc.netty.protocol;

/**
 * RpcResponse builder
 * @author CrazyPig
 *
 */
public final class RpcResponseBuilder {
    
    private RpcResponseBuilder() {}
    
    public static RpcResponseBuilder newBuilder() {
        return new RpcResponseBuilder();
    }
    
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
    
    public RpcResponseBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
    public RpcResponseBuilder setCode(int code) {
        this.code = code;
        return this;
    }
    
    public RpcResponseBuilder setResult(Object result) {
        this.result = result;
        return this;
    }
    
    public RpcResponse build() {
        
        RpcResponse response = new RpcResponse();
        response.setRequestId(this.requestId);
        response.setCode(this.code);
        response.setResult(this.result);
        
        return response;
    }
    
    

}
