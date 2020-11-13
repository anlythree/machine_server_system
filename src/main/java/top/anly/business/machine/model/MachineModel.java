package top.anly.business.machine.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import top.anly.business.log.domain.LogDesc;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.enums.MachineDescStatusEnum;
import top.anly.business.machine.service.impl.MachineDescServiceImpl;
import top.anly.business.runpart.domain.RunPart;
import top.anly.business.runpart.service.impl.RunPartServiceImpl;
import top.anly.common.util.SpringContextBeanService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备操作模型
 *
 * @author wangli
 * @date 2020/11/11 10:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineModel {

    private MachineDesc machineDesc;

    private List<LogDesc> logDescList;

    private RunPart runPart;

    private MachineDescServiceImpl machineDescService;

    private RunPartServiceImpl runPartService;

    /**
     * 更新缓存中的状态和时间
     */
    private void updateMachineMap() {
        MachineDescServiceImpl.machineDescMap.put(this.getMachineDesc().getMachineName(),
                new MachineStatusModel(this.getMachineDesc().getMachineStatus(), LocalDateTime.now()));
    }

    /**
     * 初始化机器设备操作模型
     * @param machineDesc
     */
    public MachineModel(MachineDesc machineDesc) {
        this.machineDesc = machineDesc;
        // 大多数情况是一条记录，一条运行时间段记录
        logDescList = new ArrayList<>(1);
        this.machineDescService= SpringContextBeanService.getBean(MachineDescServiceImpl.class);
        this.runPartService = SpringContextBeanService.getBean(RunPartServiceImpl.class);
    }


    /**
     * 开机
     * @return
     */
    public MachineModel openMachine(){
        // 修改状态为暂停
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条开机记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"开机"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }


    /**
     * 关机
     * @return
     */
    public MachineModel closeMachine(){
        // 修改状态为关机状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.SHUT_DOWN.getStatusNum());
        // 添加一条关机记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"关机"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }


    /**
     * 开始运行
     * @return
     */
    public MachineModel startRun(){
        // 修改状态为运行状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.RUNNING.getStatusNum());
        // 添加一条开始运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"开始运行"));
        // 添加一条时间段记录
        this.setRunPart(new RunPart(this.getMachineDesc()));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }

    /**
     * 运行结束
     * @return
     */
    public MachineModel stopRun(){
        // 修改状态为暂停状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"运行结束"));
        // 查询原来的运行时间段记录
        LambdaQueryWrapper<RunPart> runPartLambdaQueryWrapper = Wrappers.lambdaQuery();
        runPartLambdaQueryWrapper.eq(RunPart::getMachineId,this.getMachineDesc().getId())
                .isNull(RunPart::getEndTime)
                .orderByDesc(RunPart::getStartTime)
                .last("limit 1;");
        RunPart runPartStart = runPartService.getOne(runPartLambdaQueryWrapper);
        // 添加原运行时间段的结束日期和使用时间段
        this.setRunPart(new RunPart(runPartStart));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }

    /**
     * 开始报警
     * @return
     */
    public MachineModel startError(){
        // 修改状态为报警状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.ERROR.getStatusNum());
        // 添加一条运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"开始报警"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }

    /**
     * 报警结束
     * @return
     */
    public MachineModel stopError(){
        // 修改状态为暂停状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"报警结束"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }

    /**
     * 上线
     * @return
     */
    public MachineModel onLine(){
        // 修改状态为暂停状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"上线"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }

    /**
     * 下线
     * @return
     */
    public MachineModel offLine(){
        // 修改状态为暂停状态
        this.getMachineDesc().setMachineStatus(MachineDescStatusEnum.NOT_ONLINE.getStatusNum());
        // 添加一条运行记录
        this.getLogDescList().add(new LogDesc(this.getMachineDesc(),"离线"));
        // 修改缓存中的心跳时间和机器设备状态
        updateMachineMap();
        return this;
    }


}