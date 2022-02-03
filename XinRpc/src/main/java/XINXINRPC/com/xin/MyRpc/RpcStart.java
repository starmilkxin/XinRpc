package XINXINRPC.com.xin.MyRpc;

import XINXINRPC.com.xin.MyRpc.core.NettyClient;
import XINXINRPC.com.xin.MyRpc.core.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class RpcStart implements CommandLineRunner {
    @Autowired
    private NettyServer nettyServer;

    @Autowired
    private NettyClient nettyClient;

    @Override
    public void run(String... args) throws Exception {
        //将带有注解XinReference的属性注入为代理对象
        nettyClient.injectProxyObject();
        //服务的注册，以及服务端的启动
        nettyServer.automaticRegisterAndStartServer();
    }
}
