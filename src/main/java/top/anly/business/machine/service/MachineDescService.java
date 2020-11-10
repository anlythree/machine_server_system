package top.anly.business.machine.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import top.anly.business.machine.domain.MachineDesc;

/**
 * @author wangli
 * @date 2020/11/9 17:14
 */
public interface MachineDescService {

    /**
     * 接受esp8266的mqtt消息
     *
     * @param topic
     * @param mqttMessage
     */
    void record(MqttMessage mqttMessage,String topic);

    /**
     * 通过设备名称查询设别信息
     * @param machineName
     * @return
     */
    MachineDesc getMachineDescByName(String machineName);


    /**
     * 缓存设备信息到类中的map中（回调）
     */
    void cacheMachineInfo();
}
