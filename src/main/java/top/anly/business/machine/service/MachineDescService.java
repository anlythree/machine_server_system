package top.anly.business.machine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.model.MachineStatusModel;

import java.util.List;
import java.util.Map;

/**
 * @author anlythree
 * @date 2020/11/9 17:14
 */
public interface MachineDescService extends IService<MachineDesc> {

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

    /**
     * 查询所有设备信息
     * @return
     */
    List<MachineDesc> queryMachineInfo();

    /**
     * 查询缓存中的设备信息
     * @return
     */
    Map<String, MachineStatusModel> queryMachineInfoCache();

    /**
     * 手动缓存设备信息并返回缓存结果
     * @return
     */
    Map<String, MachineStatusModel> manualCacheMachineInfo();

    /**
     * 查询缓存的设备信息
     * @return
     */
    Map<String, MachineStatusModel> getMachineDescMap();

    /**
     * 更新缓存
     * @param key
     * @param value
     * @return
     */
    void setMachineDescMap(String key,MachineStatusModel value);


    /**
     * 开启或关闭离线检测
     * @return
     */
    String openOrCloseOnlineCheck();

    /**
     * 删掉log和运行时间段记录
     */
    void deleteLogAndRunPart();
}
