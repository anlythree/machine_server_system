package top.anly.business.machine.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;

/**
 * @author wangli
 * @date 2020/11/9 17:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineRecordJsonModel {

    private Integer machineStatus;

    public MachineRecordJsonModel(MqttMessage mqttMessage) {
        MachineRecordJsonModel machineRecordJsonModel = JSONObject.parseObject(mqttMessage.toString(), MachineRecordJsonModel.class);
        this.machineStatus = machineRecordJsonModel.machineStatus;
    }
}
