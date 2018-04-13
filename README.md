## oh-netty-rpc

[Tips] Just for learning RPC ! 基于netty4实现的简单rpc client和rpc server, 使用zk做服务注册和服务发现

## Project module introduction

各子模块说明:

- oh-netty-rpc-server : 基于netty实现的RPC server，默认加载了`oh-netty-rpc-demoservice`的接口，用于测试服务接口调用
- oh-netty-rpc-client : 基于netty实现的RPC client，使用JDK动态代理实现RPC client，隐藏了底层实现细节，使服务调用看起来像是本地调用(网络通讯、编解码、远程调用)
- oh-netty-rpc-protocol : 封装了RPC通讯之间约定的request和response格式
- oh-netty-rpc-registry : 服务注册和服务发现，提供基于zk的实现
- oh-netty-rpc-demoservice : 用于测试的服务接口，实现类在`oh-netty-rpc-server`模块中，`oh-netty-rpc-client`直接使用该接口进行远程调用
- oh-netty-rpc-test : 提供一个简单的测试，测试RPC server和RPC client之间的调用（服务注册和服务发现使用zk）

## Futures

- 内置使用kryo序列化，同时支持protostuff和原生jdk序列化方式（没有放开配置）
- 简单的服务注册与服务发现功能，基于zk
- client端简单的异步RPC调用支持，使用方式请查看下一节介绍

## Tips:

1. 序列化目前支持kryo（默认，写死了配置）、protostuff和jdk原生序列化
2. 服务注册和服务发现只是简单基于ip和host实现

## ClientSide Async RPC Support

client端定义原始service扩展接口，配合注解，实现简单的异步RPC方法调用，目前支持两种方式：Callback和Future，举例说明:

假设接口 `HelloSerivce`

```java
public interface HelloService {
    String sayHello(String name);
}
```

client端想异步调用sayHello方法，则在client端需要新增接口，继承该接口，并配合注解定义异步RPC调用方法:

```java
@AsyncRpc
public interface HelloServiceAsync extends HelloService {
    
    @AsyncRpcMethod(origin = "sayHello", type = AsyncRpcMethodType.CALLBACK)
    void sayHelloAsync(String name, Callback clientCallback);

    @AsyncRpcMethod(origin = "sayHello", type =  AsyncRpcMethodType.FUTURE)
    Future sayHelloAsync(String name);
}
```

注解`@AsyncRpc`用于标识一个接口中定义了异步RPC接口方法，同时需要配合注解`@AsyncRpcMethod`对接口方法进行标识，标识该接口方法为异步PRC接口， `@AsyncRpcMethod`注解中的配置如下所示：

- origin : 指定原接口方法名
- type : 异步RPC接口方式，目前支持客户端提供callback方式和返回java Future方式

最后附上一个使用demo:

```java
RpcClient client = ...;
client.init();
// 创建代理
HelloServiceAsync asyncInterface = client.createServiceProxy(HelloServiceAsync.class);

// callback方式调用
asyncInterface.sayHelloAsync("CrazyPig", new RpcInvocationCallback<String>() {
	
	@Override
	public void onSuccess(String result) {
	    logger.info("on sayHelloAgainAsync callback, get result : {}", result);
	}
	
	@Override
	public void onFailure(Throwable t) {
	    logger.info("on sayHelloAgainAsync callback, get error : {}", t.getMessage());
	    logger.error(t.getMessage(), t);
	}
});

// future方式调用
Future<String> future = asyncInterface.sayHelloAsync("CrazyPig");
// future.get(); 
```

## Getting Start

- 安装zk并启动
- 跑RpcServer，主类为`RpcServerMain`，使用Spring加载RpcServer，配置请看`applicationContext-rpcServer.xml`
- 跑RpcClient，主类为`RpcClientMain`，使用Spring加载RpcClient，配置请看`applicationContext-rpcClient.xml`
