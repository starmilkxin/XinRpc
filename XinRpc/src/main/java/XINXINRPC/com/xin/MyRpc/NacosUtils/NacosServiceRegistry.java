package XINXINRPC.com.xin.MyRpc.NacosUtils;

import XINXINRPC.com.xin.MyRpc.LoadBalancer.CommonLoadBalancer;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Value("${XinRpc.RegistryAdd}")
    private String registryAdd;

    @Value("${XinRpc.RegistryPort}")
    private Integer registryPort;

    @Value("${XinRpc.LoadBalancer}")
    private Integer loadBalancerCode;

    private static NamingService namingService;

    //存放已经注册过服务的接口名称
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void connect() {
        try {
            namingService = NacosFactory.createNamingService(registryAdd + ":" + registryPort);
            logger.info("连接Nacos[" + registryAdd + ":" + registryPort + "]");
        }catch (Exception e) {
            throw new RuntimeException("Nacos[" + registryAdd + ":" + registryPort + "]" + "连接失败");
        }
    }

    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
            registeredService.add(serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("Nacos服务[" + serviceName + "]注册失败");
        }
    }

    public static void deregister(InetSocketAddress inetSocketAddress) {
        for (String serviceName : registeredService) {
            try {
                namingService.deregisterInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                logger.info("成功注销服务[" + serviceName + "] 地址[" + inetSocketAddress + "]");
            } catch (NacosException e) {
                throw new RuntimeException("Nacos服务注销失败");
            }
        }
    }

    @Override
    public InetSocketAddress getServiceAdd(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            //负载均衡
            CommonLoadBalancer loadBalancer = CommonLoadBalancer.getLoadBalancer(loadBalancerCode);
            Instance instance = loadBalancer.select(instances, serviceName);
            logger.info("选取服务器: {}", instance.getIp());
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            throw new RuntimeException("Nacos获取服务失败");
        }
    }
}
