package com.crazypig.rpc.netty.protocol;

/**
 * RpcRequest builder
 * @author CrazyPig
 *
 */
public final class RpcRequestBuilder {
    
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 请求服务类全名
     */
    private String serviceName;
    /**
     * 请求方法名
     */
    private String methodName;
    /**
     * 请求参数类型
     */
    private String[] paramTypes;
    /**
     * 请求参数值
     */
    private Object[] paramValues;
    
    private RpcRequestBuilder() {}
    
    public static RpcRequestBuilder newBuilder() {
        return new RpcRequestBuilder();
    }
    
    public RpcRequestBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
    public RpcRequestBuilder setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public RpcRequestBuilder setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }
    
    public RpcRequestBuilder setParamTypes(String[] paramTypes) {
        this.paramTypes = paramTypes;
        return this;
    }
    
    public RpcRequestBuilder setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
        return this;
    }
    
    public RpcRequest build() {
        RpcRequest request = new RpcRequest();
        request.setRequestId(this.requestId);
        request.setServiceName(this.serviceName);
        request.setMethodName(this.methodName);
        request.setParamTypes(this.paramTypes);
        request.setParamValues(this.paramValues);
        return request;
    }
    
}
