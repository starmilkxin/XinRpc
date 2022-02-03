package XINXINRPC.com.xin.MyRpc.config;

import XINXINRPC.com.xin.MyRpc.Myannotation.XinService;
import XINXINRPC.com.xin.MyRpc.core.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(MyBeanFactoryPostProcessor.class);

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //对于服务端
        //扫描XinService注解，得到需要注册服务的类
        Map<String, Object> waitRegisteredService = configurableListableBeanFactory.getBeansWithAnnotation(XinService.class);
        //将这些服务类实体放入到已经被装配到IOC中的NettyServer
        NettyServer.addwaitRegisterService(waitRegisteredService);
//        AnnotationScanner scanner = AnnotationScanner.getScanner((BeanDefinitionRegistry) configurableListableBeanFactory, XinService.class);
//        String property = applicationContext.getEnvironment().getProperty("XinRpc.ServiceScan");;
//        scanner.setResourceLoader(applicationContext);
//        int already = scanner.scan(property);
//        logger.info(already + "个XinService被扫描注入");

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
