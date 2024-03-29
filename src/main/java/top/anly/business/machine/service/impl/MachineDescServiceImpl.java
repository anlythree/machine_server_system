package top.anly.business.machine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.anly.business.log.domain.LogDesc;
import top.anly.business.log.service.LogDescService;
import top.anly.business.machine.dao.MachineDescDao;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.enums.MachineDescStatusEnum;
import top.anly.business.machine.model.MachineModel;
import top.anly.business.machine.model.MachineRecordJsonModel;
import top.anly.business.machine.model.MachineStatusModel;
import top.anly.business.machine.service.MachineDescService;
import top.anly.business.runpart.domain.RunPart;
import top.anly.business.runpart.service.RunPartService;
import top.anly.business.task.InitializeTask;
import top.anly.common.mqtt.MessageThread;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anlythree
 * @date 2020/11/9 17:20
 */
@Slf4j
@Service
public class MachineDescServiceImpl extends ServiceImpl<MachineDescDao, MachineDesc> implements MachineDescService {

    /**
     * spring启动类中进行初始化操作，
     * 此map为设备的最新状态和最近心跳时间
     */
    public static Map<String, MachineStatusModel> machineDescMap = new HashMap<>();

    @Resource
    private LogDescService logDescService;

    @Resource
    private RunPartService runPartService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(MqttMessage mqttMessage, String topic) {
        // 解析消息中的电脑横机信息
        // 电脑横机数据采集信息
        MachineRecordJsonModel machineRecordJsonModel = new MachineRecordJsonModel(mqttMessage);
        // 电脑横机名称(topic最后一个斜杠后是电脑横机的名称)
        String machineName = topic.replace(MessageThread.MACHINE_TOPIC, "");
        log.info(machineName+"心跳");
        // 场景判断
        if (machineDescMap.get(machineName).getMachineStatus().equals(machineRecordJsonModel.getMachineStatus())) {
            // 设备状态和两秒前相同，刷新最新心跳时间，然后退出整个逻辑
            refreshHeartTime(machineName);
            return;
        }
        //查询之前的状态
        MachineDesc machineDescOld = getMachineDescByName(machineName);
        if (null == machineDescOld) {
            // 查询不到机器设备名称就提示app端该设备未维护基本信息
            // todo-anly

        }
        // 记录相关动作
        takeNotes(machineRecordJsonModel, machineDescOld);
    }

    /**
     * 记录相关动作
     *
     * @param machineRecordJsonModel
     * @param machineDescOld
     */
    public void takeNotes(MachineRecordJsonModel machineRecordJsonModel, MachineDesc machineDescOld) {
        // 横机设备操作模型初始化
        MachineModel machineModel = null;
        String machineName = machineDescOld.getMachineName();
        if (MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineDescOld.getMachineStatus())) {
            // 之前为运行状态
            if (MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 停止运行+关机
                machineModel = new MachineModel(machineDescOld)
                        .stopRun()
                        .closeMachine();
                log.info(machineName + ":运行->关机");
            } else if (MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 结束运行
                machineModel = new MachineModel(machineDescOld)
                        .stopRun();
                log.info(machineName + ":运行->暂停");
            } else if (MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 结束运行+开始报警
                machineModel = new MachineModel(machineDescOld)
                        .stopRun()
                        .startError();
                log.info(machineName + ":运行->报警");
            }
        } else if (MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineDescOld.getMachineStatus())) {
            // 之前为关机状态
            if (MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开机+开始运行
                machineModel = new MachineModel(machineDescOld)
                        .openMachine()
                        .startRun();
                log.info(machineName + ":关机->运行");
            } else if (MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开机
                machineModel = new MachineModel(machineDescOld)
                        .openMachine();
                log.info(machineName + ":关机->暂停");
            } else if (MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开机+开始报警
                machineModel = new MachineModel(machineDescOld)
                        .openMachine()
                        .startError();
                log.info(machineName + ":关机->报警");
            }
        } else if (MachineDescStatusEnum.PENDING.getStatusNum().equals(machineDescOld.getMachineStatus())) {
            // 之前为暂停状态
            if (MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开始运行
                machineModel = new MachineModel(machineDescOld)
                        .startRun();
                log.info(machineName + ":暂停->运行");
            } else if (MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 关机
                machineModel = new MachineModel(machineDescOld)
                        .closeMachine();
                log.info(machineName + ":暂停->关机");
            } else if (MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开始报警
                machineModel = new MachineModel(machineDescOld)
                        .startError();
                log.info(machineName + ":暂停->报警");
            }
        } else if (MachineDescStatusEnum.ERROR.getStatusNum().equals(machineDescOld.getMachineStatus())) {
            // 之前为报警状态
            if (MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 停止报警+开始运行
                machineModel = new MachineModel(machineDescOld)
                        .stopError()
                        .startRun();
                log.info(machineName + ":报警->运行");
            } else if (MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 停止报警+关机
                machineModel = new MachineModel(machineDescOld)
                        .stopError()
                        .closeMachine();
                log.info(machineName + ":报警->关机");
            } else if (MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 停止报警
                machineModel = new MachineModel(machineDescOld)
                        .stopError();
                log.info(machineName + ":报警->暂停");
            }
        } else if (MachineDescStatusEnum.NOT_ONLINE.getStatusNum().equals(machineDescOld.getMachineStatus())) {
            // 之前为离线状态
            if (MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 上线+开始运行
                machineModel = new MachineModel(machineDescOld)
                        .openMachine()
                        .startRun();
                log.info(machineName + ":离线->运行");
            } else if (MachineDescStatusEnum.SHUT_DOWN.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 关机
                machineModel = new MachineModel(machineDescOld)
                        .closeMachine();
                log.info(machineName + ":离线->关机");
            } else if (MachineDescStatusEnum.PENDING.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开机
                machineModel = new MachineModel(machineDescOld)
                        .openMachine();
                log.info(machineName + ":离线->暂停");
            } else if (MachineDescStatusEnum.ERROR.getStatusNum().equals(machineRecordJsonModel.getMachineStatus())) {
                // 开机+报警
                machineModel = new MachineModel(machineDescOld)
                        .openMachine()
                        .startError();
                log.info(machineName + ":离线->报警");
            }
        }
        if (null == machineModel) {
            // 无法判断电脑横机动作
            // todo-anly 发送异常信息给app端
            log.error(machineName + "当前的状态码不识别:" + machineRecordJsonModel.getMachineStatus());
            return;
        }
        // 持久化数据
        persistence(machineModel);
    }

    /**
     * 持久化数据
     * @param machineModel
     */
    private void persistence(MachineModel machineModel) {
        // 更新数据库中的数据
        // 1、更新库中设备状态
        updateById(machineModel.getMachineDesc());
        // 2、添加库中事件记录
        if (CollectionUtils.isNotEmpty(machineModel.getLogDescList())) {
            logDescService.saveBatch(machineModel.getLogDescList());
        }
        // 3、修改运行时间段
        if (null != machineModel.getRunPart()) {
            runPartService.saveOrUpdate(machineModel.getRunPart());
        }
        // 4、修改缓存中的心跳时间和机器设备状态
        machineModel.updateMachineMap();
    }

    /**
     * 直接更新心跳刷新时间
     * @param machineName
     */
    private void refreshHeartTime(String machineName) {
        MachineStatusModel machineStatusModel = machineDescMap.get(machineName);
        machineStatusModel.setLastHeartTime(LocalDateTime.now());
        machineDescMap.put(machineName, machineStatusModel);
    }

    @Override
    public MachineDesc getMachineDescByName(String machineName) {
        LambdaQueryWrapper<MachineDesc> machineDescLambdaQueryWrapper = Wrappers.lambdaQuery();
        machineDescLambdaQueryWrapper.eq(MachineDesc::getMachineName, machineName)
                .last("limit 1;");
        return getOne(machineDescLambdaQueryWrapper);
    }

    @Override
    public void cacheMachineInfo() {
        // 初始化内存中的设备状态更新时间
        List<MachineDesc> list = list();
        for (MachineDesc machineDesc : list) {
            // 初始化设备状态为离线状态
            machineDescMap.put(machineDesc.getMachineName(), new MachineStatusModel(MachineDescStatusEnum.NOT_ONLINE.getStatusNum()));
        }
        log.info("所有设备信息已缓存至map中");
        log.info("map内容:" + machineDescMap.toString());
    }

    @Override
    public List<MachineDesc> queryMachineInfo() {
        return list();
    }

    @Override
    public Map<String, MachineStatusModel> queryMachineInfoCache() {
        return machineDescMap;
    }

    @Override
    public Map<String, MachineStatusModel> manualCacheMachineInfo() {
        cacheMachineInfo();
        return machineDescMap;
    }

    @Override
    public Map<String, MachineStatusModel> getMachineDescMap() {
        return machineDescMap;
    }

    @Override
    public void setMachineDescMap(String key, MachineStatusModel value) {
        machineDescMap.put(key, value);
    }

    @Override
    public String openOrCloseOnlineCheck() {
        if(InitializeTask.IS_ChECK){
            InitializeTask.IS_ChECK = false;
            log.info("关闭离线监控成功!");
            return "关闭离线监控成功!";
        }else {
            InitializeTask.IS_ChECK = true;
            log.info("开启离线监控成功!");
            return "开启离线监控成功!";
        }
    }

    @Override
    public void deleteLogAndRunPart() {
        LambdaQueryWrapper<LogDesc> logDescLambdaQueryWrapper = Wrappers.lambdaQuery();
        logDescLambdaQueryWrapper.last(" where true;");
        logDescService.remove(logDescLambdaQueryWrapper);
        LambdaQueryWrapper<RunPart> runPartLambdaQueryWrapper = Wrappers.lambdaQuery();
        runPartLambdaQueryWrapper.last("where true");
        runPartService.remove(runPartLambdaQueryWrapper);
    }
}
