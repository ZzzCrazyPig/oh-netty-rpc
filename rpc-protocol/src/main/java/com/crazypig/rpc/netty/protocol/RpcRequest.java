package com.crazypig.rpc.netty.protocol;

import java.io.Serializable;

/**
 * 封装rpc请求
 * @author CrazyPig
 *
 */
public class RpcRequest implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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
    
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public String[] getParamTypes() {
        return paramTypes;
    }
    public void setParamTypes(String[] paramTypes) {
        this.paramTypes = paramTypes;
    }
    public Object[] getParamValues() {
        return paramValues;
    }
    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
    
}
