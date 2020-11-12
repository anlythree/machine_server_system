package top.anly.business.machine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.anly.business.machine.dao.MachineDescDao;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.enums.MachineDescStatusEnum;
import top.anly.business.machine.model.MachineRecordJsonModel;
import top.anly.business.machine.model.MachineStatusModel;
import top.anly.business.machine.service.MachineDescService;
import top.anly.common.mqtt.MessageThread;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangli
 * @date 2020/11/9 17:20
 */
@Slf4j
@Service
public class MachineDescServiceImpl extends ServiceImpl<MachineDescDao,MachineDesc> implements MachineDescService {

    /**
     * spring启动类中进行初始化操作，
     * 此map为设备的最新状态和最近心跳时间
     */
    public static Map<String, MachineStatusModel> machineDescMap = new HashMap<>();


    @Resource
    private MachineDescDao machineDescDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(MqttMessage mqttMessage,String topic){
        // 解析消息中的电脑横机信息
        // 电脑横机数据采集信息
        MachineRecordJsonModel machineRecordJsonModel = new MachineRecordJsonModel(mqttMessage);
        // 电脑横机名称(topic最后一个斜杠后是电脑横机的名称)
        String machineName = topic.replace(MessageThread.MACHINE_TOPIC, "");
        // 场景判断
        if(machineDescMap.get(machineName).getMachineStatus().equals(machineRecordJsonModel.getMachineStatus())){
            // 设备状态和两秒前相同，刷新最新心跳时间，然后退出整个逻辑
            refreshHeartTime(machineName);
            return;
        }
        //查询之前的状态
        MachineDesc machineDescOld = getMachineDescByName(machineName);
        // 全局设备模型对象
        if(MachineDescStatusEnum.NOT_ONLINE.getStatusNum().equals(machineDescOld.getMachineStatus())){
            // 如果之前宕机了，那么先把状态改成关机状态
            machineDescOld.setMachineStatus(MachineDescStatusEnum.SHUT_DOWN.getStatusNum());
            machineDescOld.setGmtModified(LocalDateTime.now());
        }
        if(MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineDescOld.getMachineStatus())){
            // 之前为运行状态
            if(MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("运行->关机");
            }else if(MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("运行->暂停");
            }else if(MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("运行->报警");
            }else {
                log.error(machineName+"当前的状态码不识别:"+machineRecordJsonModel.getMachineStatus());
            }
        }else if(MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineDescOld.getMachineStatus())){
            // 之前为关闭状态
            if(MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("关机->运行");
            }else if(MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("关机->暂停");
            }else if(MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("关机->报警");
            }else {
                log.error(machineName+"当前的状态码不识别:"+machineRecordJsonModel.getMachineStatus());
            }
        }else if(MachineDescStatusEnum.PENDING.getStatusNum().equals(machineDescOld.getMachineStatus())){
            // 之前为暂停状态
            if(MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("暂停->运行");
            }else if(MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("暂停->关机");
            }else if(MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("暂停->报警");
            }else {
                log.error(machineName+"当前的状态码不识别:"+machineRecordJsonModel.getMachineStatus());
            }
        }else if(MachineDescStatusEnum.ERROR.getStatusNum().equals(machineDescOld.getMachineStatus())){
            // 之前为报警状态
            if(MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("报警->运行");
            }else if(MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("报警->关机");
            }else if(MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())){
                log.info("报警->暂停");
            }else {
                log.error(machineName+"当前的状态码不识别:"+machineRecordJsonModel.getMachineStatus());
            }
        }else {
            log.error("查询"+machineName+"之前的状态,不识别的设备状态码:"+machineDescOld.getMachineStatus());
        }
    }

    /**
     * 直接更新心跳刷新时间
     * @param machineName
     */
    private void refreshHeartTime(String machineName) {
        MachineStatusModel machineStatusModel = machineDescMap.get(machineName);
        machineStatusModel.setLastHeartTime(LocalDateTime.now());
        machineDescMap.put(machineName,machineStatusModel);
    }

    @Override
    public MachineDesc getMachineDescByName(String machineName) {
        LambdaQueryWrapper<MachineDesc> machineDescLambdaQueryWrapper = Wrappers.lambdaQuery();
        machineDescLambdaQueryWrapper.eq(MachineDesc::getMachineName,machineName)
                .last("limit 1;");
        return getOne(machineDescLambdaQueryWrapper);
    }

    @Override
    public void cacheMachineInfo(){
        // 初始化内存中的设备状态更新时间
        List<MachineDesc> list = list();
        for (MachineDesc machineDesc : list) {
            // 初始化设备状态为离线状态
            machineDescMap.put(machineDesc.getMachineName(),new MachineStatusModel(MachineDescStatusEnum.NOT_ONLINE.getStatusNum()));
        }
        log.info("所有设备信息已缓存至map中");
        log.info("map内容:"+machineDescMap.toString());
    }
}
