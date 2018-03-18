## oh-netty-rpc

[Tips] Just for learning RPC ! 基于netty4实现的简单rpc client和rpc server, 使用zk做服务注册和服务发现

## project module introduction

各子模块说明:

- oh-netty-rpc-server : 基于netty实现的RPC server，默认加载了`oh-netty-rpc-demoservice`的接口，用于测试服务接口调用
- oh-netty-rpc-client : 基于netty实现的RPC client，基于JDK动态代理实现RPC client，隐藏了底层实现细节，使服务调用看起来像是本地调用(网络通讯、编解码、远程调用)
- oh-netty-rpc-protocol : 封装了RPC通讯之间约定的request和response格式
- oh-netty-rpc-registry : 服务注册和服务发现，提供基于zk的实现
- oh-netty-rpc-demoservice : 用于测试的服务接口，实现类在`oh-netty-rpc-server`模块中，`oh-netty-rpc-client`直接使用该接口进行远程调用
- oh-netty-rpc-test : 提供一个简单的测试，测试RPC server和RPC client之间的调用（服务注册和服务发现使用zk）

> Tip1: 编解码使用了JDK序列化，后期可考虑扩展支持多种序列化方式
> 
> Tip2: 服务注册和服务发现只是简单基于ip和host实现

## Getting Start

- 安装zk并启动
- 跑RpcServer，主类为`RpcServerMain`，使用Spring加载RpcServer，配置请看`applicationContext-rpcServer.xml`
- 跑RpcClient，主类为`RpcClientMain`，使用Spring加载RpcClient，配置请看`applicationContext-rpcClient.xml`