package XINXINRPC.com.xin.MyRpc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@ComponentScan({"XINXINRPC.com.xin.MyRpc"
})
public class ComponentScanConfig {

}