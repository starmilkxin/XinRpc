package XINXINRPC.com.xin.MyRpc.NacosUtils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceProvider {
    //存放接口名称和实现类的映射关系
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    //存放已经注册过服务的接口名称
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    public void setService(Object service) {
        serviceMap.put(service.getClass().getInterfaces()[0].getSimpleName(), service);
        registeredService.add(service.getClass().getInterfaces()[0].getSimpleName());
    }

    public Object getService(String interfaceName) {
        Object service = serviceMap.get(interfaceName);
        if (service == null) {
            throw new RuntimeException("未注册此服务");
        }
        return service;
    }

}
