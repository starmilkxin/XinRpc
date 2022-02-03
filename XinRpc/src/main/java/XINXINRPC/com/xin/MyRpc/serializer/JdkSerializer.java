package XINXINRPC.com.xin.MyRpc.serializer;

import java.io.*;

public class JdkSerializer implements CommonSerializer {
    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        byte[] data = null;
        try {
            outputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(obj);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时发生错误: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                logger.error("序列化时发生错误: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        ByteArrayInputStream inputStream = null;
        ObjectInputStream oInputStream = null;
        Object obj = null;
        try {
            inputStream = new ByteArrayInputStream(bytes);
            oInputStream = new ObjectInputStream(inputStream);
            obj = oInputStream.readObject();
        } catch (IOException e) {
            logger.error("反序列化时发生错误: {}", e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("反序列化时发生错误: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                oInputStream.close();
            } catch (IOException e) {
                logger.error("反序列化时发生错误: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return obj;
    }
}
