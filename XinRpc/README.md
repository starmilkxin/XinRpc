# XINRpc
XinRpc是一款基于Netty的RPC框架，实现了多种序列化算法与负载均衡算法，可以高自由度进行配置。

+ 基于Netty，实现服务间的NIO通信。
+ 采用心跳机制保持长连接。
+ 自定义协议，防止tcp粘包/拆包。
+ 多种序列化算法(1.jdk序列化，2.json序列化，3.protobuf序列化)满足不同数据的传输，优化了传输性能。
+ 多种负载均衡算法(1.加权随机负载均衡 2.轮询负载均衡 3.一致性Hash负载均衡 )解决并发压力，提高服务性能。
+ 自定义注解，上传maven仓库，便捷使用。服务端打上注解便可以自动注册服务，客户端打上注解便可以像调用本地方法一样调用远程方法。

## 使用
### 依赖

```xml
<dependency>
    <groupId>com.xin</groupId>
    <artifactId>XinRpc</artifactId>
    <version>1.2.6</version>
</dependency>
```

PS: 没有上传到中央仓库(太麻烦了...)，可以下载源码后自己maven install到本地仓库后，引入依赖。

### 配置

```properties
#application.properties

#Nacos注册中心的地址
XinRpc.RegistryAdd=127.0.0.1
#Nacos注册中心的端口
XinRpc.RegistryPort=8848
#服务端的地址
XinRpc.ServerAdd=127.0.0.1
#服务端的端口
XinRpc.ServerPort=7000

#序列化算法选择
XinRpc.Serializer=1

#负载均衡算法选择
XinRpc.LoadBalancer=1
```

在application.properties中配置好Nacos注册中心的地址与端口，服务端的地址与端口，序列化算法的选择和负载均衡算法的选择。

### 服务端注解

```java
@XinService
@Component
public class HelloServiceImp implements HelloService {
    
    @Override
    public String hello(String word) {
        System.out.println("调用了hello");
        return "hello: " + word;
    }
}
```

服务端在接口的实现类上打上@XinService注解（前提是该实现类在IOC容器中）。

### 客户端注解

```java
@RestController
public class TestHello {

    @XinReference
    private HelloService helloService;

    @RequestMapping(path="/index", method= RequestMethod.GET)
    public String userHello() {
        return helloService.hello("hellohello");
    }
}
```

客户端在接口对象上打上@XinReference注解（前提是该对象所在类在IOC容器中）。

接下来就可以直接调用对象的相应方法了。

## 负载均衡算法
* XinRpc.LoadBalancer=1 轮询算法

* XinRpc.LoadBalancer=2 加权随机算法

* XinRpc.LoadBalancer=3 一致性hash算法

默认采用加权随机算法

## 序列化算法
* XinRpc.Serializer= 1 ProtobufSerializer

* XinRpc.Serializer= 2 JdkSerializer

* XinRpc.Serializer= 3 JsonSerializer

默认采用ProtobufSerializer