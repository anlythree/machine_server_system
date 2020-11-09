package top.anly.common.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 发布消息的回调类
 * <p>
 * 必须实现MqttCallback的接口并实现对应的相关接口方法CallBack 类将实现 MqttCallBack。
 * 每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。
 * 在回调中，将它用来标识已经启动了该回调的哪个实例。
 * 必须在回调类中实现三个方法：
 * <p>
 * public void messageArrived(MqttTopic topic, MqttMessage message)接收已经预订的发布。
 * <p>
 * public void connectionLost(Throwable cause)在断开连接时调用。
 * <p>
 * public void deliveryComplete(MqttDeliveryToken token))
 * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。o
 * 由 MqttClient.connect 激活此回调。
 */
@Slf4j
public class PushCallback implements MqttCallback {

    /**
     * 在断开连接时调用。
     *
     * @param cause
     * @return
     */
    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        log.error("连接断开，可以重连");
        try {
            new MqttConnect();
            log.info("重连成功");
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("重连失败" + e);
        }
    }

    /**
     * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
     *
     * @param token
     * @return
     */

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete---------" + token.isComplete());
    }

    /**
     * 接收已经预订的发布
     *
     * @param topic
     * @param message
     * @return
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
//            String reciveMsg = new String(message.getPayload());
//            log.info(reciveMsg);
            ThreadPoolExecutor executor = ThreadPoolService.getInstance();
            MessageThread reciveThread = new MessageThread(/*reciveMsg,*/ topic, message);
            log.info(executor.hashCode() + "");
            executor.execute(reciveThread);
            log.info("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                    executor.getQueue().size() + "，已执行完别的任务数目：" + executor.getCompletedTaskCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}