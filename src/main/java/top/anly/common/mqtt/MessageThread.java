package top.anly.common.mqtt;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.transaction.annotation.Transactional;
import top.anly.business.machine.service.MachineDescService;
import top.anly.common.util.SpringContextBeanService;

/**
 * 线程类处理数采数据
 *
 * @author wangli
 * @date 2020/11/09 9:31
 */
@Slf4j
public class MessageThread implements Runnable {

    public static final String MACHINE_TOPIC = "anly/machine/";

    private String topic;

    private volatile MqttMessage message;

    private MqttConnect mqttConnect;

    private MachineDescService machineDescService;


    /**
     * 构造方法
     *
     * @param
     * @return
     */
    public MessageThread(String topic, MqttMessage message) {
        this.topic = topic;
        this.message = message;
        this.mqttConnect = SpringContextBeanService.getBean(MqttConnect.class);
        this.machineDescService = SpringContextBeanService.getBean(MachineDescService.class);
    }

    /**
     * 线程run方法(接收到消息就会进入这个方法)
     *
     * @param
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void run() {
        // 订阅的消息
        synchronized (message) {
            if(topic.startsWith(MACHINE_TOPIC)){
                // 数据处理
                machineDescService.record(message,topic);
            }
        }
    }
}
