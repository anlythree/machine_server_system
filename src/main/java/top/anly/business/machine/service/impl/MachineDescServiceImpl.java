package top.anly.business.machine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import top.anly.business.machine.dao.MachineDescDao;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.service.MachineDescService;
import javax.annotation.Resource;

/**
 * @author wangli
 * @date 2020/11/9 17:20
 */
@Slf4j
@Service
public class MachineDescServiceImpl extends ServiceImpl<MachineDescDao,MachineDesc> implements MachineDescService {

    @Resource
    private MachineDescDao machineDescDao;

    @Override
    public void record(MqttMessage mqttMessage) {

    }
}
