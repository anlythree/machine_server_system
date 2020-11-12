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

import java.util.ArrayList;
import java.util.List;

/**
 * 设备操作类
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

    public MachineModel(MachineDesc machineDesc) {
        this.machineDesc = machineDesc;
        // 大多数情况是一条记录，一条运行时间段记录
        logDescList = new ArrayList<>(1);
        this.machineDescService= SpringContextBeanService.getBean(MachineDescServiceImpl.class);
        this.runPartService = SpringContextBeanService.getBean(RunPartServiceImpl.class);
    }


    /**
     * 开机
     * @param machineModel
     * @return
     */
    public MachineModel openMachine(MachineModel machineModel){
        // 修改状态为暂停
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条开机记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"开机"));
        return machineModel;
    }

    /**
     * 关机
     * @param machineModel
     * @return
     */
    public MachineModel closeMachine(MachineModel machineModel){
        // 修改状态为关机状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.SHUT_DOWN.getStatusNum());
        // 添加一条关机记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"关机"));
        return machineModel;
    }


    /**
     * 开始运行
     * @param machineModel
     * @return
     */
    public MachineModel startRun(MachineModel machineModel){
        // 修改状态为运行状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.RUNNING.getStatusNum());
        // 添加一条开始运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"开始运行"));
        // 添加一条时间段记录
        machineModel.setRunPart(new RunPart(machineModel.getMachineDesc()));
        return machineModel;
    }

    /**
     * 运行结束
     * @return
     */
    public MachineModel stopRun(MachineModel machineModel){
        // 修改状态为暂停状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"运行结束"));
        // 查询原来的运行时间段记录
        LambdaQueryWrapper<RunPart> runPartLambdaQueryWrapper = Wrappers.lambdaQuery();
        runPartLambdaQueryWrapper.eq(RunPart::getMachineId,machineModel.getMachineDesc().getId())
                .isNull(RunPart::getEndTime)
                .orderByDesc(RunPart::getStartTime)
                .last("limit 1;");
        RunPart runPartStart = runPartService.getOne(runPartLambdaQueryWrapper);
        // 添加原运行时间段的结束日期和使用时间段
        machineModel.setRunPart(new RunPart(runPartStart));
        return machineModel;
    }

    /**
     * 开始报警
     * @param machineModel
     * @return
     */
    public MachineModel startError(MachineModel machineModel){
        // 修改状态为报警状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.ERROR.getStatusNum());
        // 添加一条运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"开始报警"));
        return machineModel;
    }

    /**
     * 报警结束
     * @param machineModel
     * @return
     */
    public MachineModel stopError(MachineModel machineModel){
        // 修改状态为暂停状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"报警结束"));
        return machineModel;
    }

    /**
     * 上线
     * @param machineModel
     * @return
     */
    public MachineModel onLine(MachineModel machineModel){
        // 修改状态为暂停状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.PENDING.getStatusNum());
        // 添加一条运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"上线"));
        return machineModel;
    }

    /**
     * 上线
     * @param machineModel
     * @return
     */
    public MachineModel offLine(MachineModel machineModel){
        // 修改状态为暂停状态
        machineModel.getMachineDesc().setMachineStatus(MachineDescStatusEnum.NOT_ONLINE.getStatusNum());
        // 添加一条运行记录
        machineModel.getLogDescList().add(new LogDesc(machineModel.getMachineDesc(),"离线"));
        return machineModel;
    }


}
