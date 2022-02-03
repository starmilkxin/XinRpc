package XINXINRPC.com.xin.MyRpc.NacosUtils;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    void register(String servicename, InetSocketAddress inetSocketAddress);

    static void deregister(InetSocketAddress inetSocketAddress) {
        
    }

    InetSocketAddress getServiceAdd(String servicename);
}
