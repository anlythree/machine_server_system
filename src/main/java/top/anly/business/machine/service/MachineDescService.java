package top.anly.business.machine.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author wangli
 * @date 2020/11/9 17:14
 */
public interface MachineDescService {

    /**
     * 记录电脑横机状态变化
     *
     * @param mqttMessage
     */
    public void record(MqttMessage mqttMessage);
}
