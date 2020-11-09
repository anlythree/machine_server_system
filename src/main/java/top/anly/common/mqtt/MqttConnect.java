package top.anly.common.mqtt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * mqtt
 *
 * @author wangli
 * @date 2020-11-09
 */
@Lazy
@Data
@Slf4j
@Component
@DependsOn("springContextBeanService")
public class MqttConnect {

    /**
     * MQTT服务
     */
//    public static String HOST = "tcp://localhost:1883";//prd
    public static String HOST = "tcp://103.72.146.31:1883";//dev


    /**
     * 定义一个主题EVER/HY/COLLECT/盒子/设备
     */
    public static final String TOPIC_COLLECT = "anly/machine/#";

    /**
     * 定义MQTT的ID,可以在MQTT服务配置中指定
     */
    private static String CLIENT_ID;
    private MqttClient myClient;
    private MqttTopic publishTopic;

    /**
     * 构造函数
     *
     * @throws MqttException
     */
    public MqttConnect() throws MqttException {
        // 每次重连更新client-id
        CLIENT_ID = "machine_server_system_" + LocalDate.now();
        myClient = new MqttClient(HOST, CLIENT_ID + UUID.randomUUID().toString(), new MemoryPersistence());
        connect();
        log.warn("MQTT服务器连接成功-----------");
        System.out.println("MQTT服务器连接成功-----------");
        //订阅采集消息
        myClient.subscribe(TOPIC_COLLECT, 0);
        log.warn("------订阅成功" + TOPIC_COLLECT);
    }

    /**
     * 连接mqtt服务器
     *
     * @param
     * @param
     */
    private void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        // 为了不断线后重连不上
        options.setCleanSession(false);
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(90);
        try {
            myClient.setCallback(new PushCallback());
            myClient.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            new MqttConnect();
        }
    }

    /**
     * 发布消息
     *
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException,
            MqttException {
        MqttDeliveryToken token = new MqttDeliveryToken();
        try {
            token = topic.publish(message);
        } catch (Exception e) {
            // 报错就修改clientId进行消息的发送
            CLIENT_ID = "wh_traceability_" + LocalDate.now();
        }
        token.waitForCompletion();
        log.info("message is published completely! "
                + token.isComplete());
    }

    /**
     * 发布服务器消息（自测使用）
     *
     * @param eqName  向这台设备发布控制指令
     * @param message 指令内容
     */
    public void publishMessageServer(String eqName, String message) {
        publishTopic = myClient.getTopic("EVER/WH/COLLECT/SERVER");
        MqttMessage myMessage = new MqttMessage();
        // todo-anly 设置mqtt消息质量
        myMessage.setQos(0);
        // todo-anly 消息不保留至下一次开机
        myMessage.setRetained(false);
        myMessage.setPayload(message.getBytes());
        try {
            publish(publishTopic, myMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
