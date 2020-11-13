package top.anly.business.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.enums.MachineDescStatusEnum;
import top.anly.business.machine.model.MachineStatusModel;
import top.anly.business.machine.service.MachineDescService;
import top.anly.business.runpart.domain.RunPart;
import top.anly.business.runpart.service.RunPartService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author anlythree
 * @date 2020/11/11 10:55
 */
@Component
@Slf4j
public class InitializeTask {

    @Autowired
    private MachineDescService machineDescService;

    @Autowired
    private RunPartService runPartService;

    /**
     * 离线检测
     * 每10秒执行一次
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0/10 * * * * ?")
    public void updateAllContractStatus() {
        log.info("机械设备离线心跳检查");
        // 遍历map
        Map<String, MachineStatusModel> machineDescMap = machineDescService.getMachineDescMap();
        List<String> machineNameUpdateStatusList = new ArrayList<>();
        for (String machineName : machineDescMap.keySet()) {
            MachineStatusModel machineStatusModel = machineDescMap.get(machineName);
            // 判断当前时间和心跳时间，相差4秒即为连接超时，设置设备离线
            Duration between = Duration.between(machineStatusModel.getLastHeartTime(), LocalDateTime.now());
            if (between.toMillis() > 4000) {
                if(MachineDescStatusEnum.RUNNING.getStatusNum().equals(machineStatusModel.getMachineStatus())){
                    // 结束正在运行的设备
                    RunPart notCloseRunPart = runPartService.getNotCloseRunPart(machineName);
                    LocalDateTime endTime = LocalDateTime.now();
                    notCloseRunPart.setEndTime(endTime);
                    notCloseRunPart.setGmtModified(endTime);
                    Duration between1 = Duration.between(notCloseRunPart.getStartTime(), endTime);
                    notCloseRunPart.setDuration(new BigDecimal(between1.toMillis()));
                    runPartService.updateById(notCloseRunPart);
                }
                // 设置缓存中的状态为离线，并更新心跳时间为当前
                machineStatusModel.setLastHeartTime(LocalDateTime.now());
                machineStatusModel.setMachineStatus(MachineDescStatusEnum.NOT_ONLINE.getStatusNum());
                machineDescService.setMachineDescMap(machineName, machineStatusModel);
                machineNameUpdateStatusList.add(machineName);
            }
        }
        if (CollectionUtils.isNotEmpty(machineNameUpdateStatusList)) {
            LambdaUpdateWrapper<MachineDesc> machineDescLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            machineDescLambdaUpdateWrapper.in(MachineDesc::getMachineName, machineNameUpdateStatusList)
                    .set(MachineDesc::getMachineStatus, MachineDescStatusEnum.NOT_ONLINE.getStatusNum());
            machineDescService.update(machineDescLambdaUpdateWrapper);
            log.info(machineNameUpdateStatusList + "被设置为离线");
        }
    }

}
