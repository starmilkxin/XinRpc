package XINXINRPC.com.xin.MyRpc.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CommonSerializer {
    Logger logger = LoggerFactory.getLogger(CommonSerializer.class);

    int PROTOSTUFF_SERIALIZER = 1;

    int JDK_SERIALIZER = 2;

    int JSON_SERIALIZER = 3;

    static CommonSerializer getSerializer(Integer code) {
        switch(code) {
            case 1:
                logger.info("装配ProtobufSerializer");
                return new ProtostuffSerializer();
            case 2:
                logger.info("装配JdkSerializer");
                return new JdkSerializer();
            case 3:
                logger.info("装配JsonSerializer");
                return new JsonSerializer();
            default:
                logger.info("默认装配ProtobufSerializer");
                return new ProtostuffSerializer();
        }
    }

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);
}
