package top.anly.business.runpart.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.anly.business.machine.domain.MachineDesc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author anlythree
 * @date 2020/11/11 10:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunPart {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private BigDecimal duration;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer machineId;

    private String machineName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    /**
     * 开始运行
     * 构造开始运行的记录实体
     * @param machineDesc
     */
    public RunPart(MachineDesc machineDesc) {
        this.startTime = LocalDateTime.now();
        this.machineId = machineDesc.getId();
        this.machineName = machineDesc.getMachineName();
        this.gmtCreate = startTime;
    }

    /**
     * 结束运行
     * 根据一个已存在的运行记录构造一条结束运行的运行记录
     * @param runPart
     */
    public RunPart(RunPart runPart){
        // 复制已确定属性
        this.id = runPart.getId();
        this.startTime = runPart.getStartTime();
        this.endTime = LocalDateTime.now();
        this.machineId = runPart.getMachineId();
        this.machineName = runPart.getMachineName();
        this.gmtCreate = runPart.getGmtCreate();
        this.gmtModified = endTime;
        // 计算运行时间
        Duration between = Duration.between(startTime, endTime);
        this.duration = new BigDecimal(between.toMillis());
    }

}
