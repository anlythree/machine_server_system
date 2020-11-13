package top.anly.business.log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.anly.business.machine.domain.MachineDesc;

import java.time.LocalDateTime;

/**
 *
 * 记录实体类
 * @author anlythree
 * @date 2020/11/11 10:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogDesc {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private String logType;

    private Integer machineId;

    private String machineName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    /**
     * 根据
     * @param logType
     * @param machineDesc
     */
    public LogDesc(MachineDesc machineDesc,String logType) {
        this.logType = logType;
        this.machineId = machineDesc.getId();
        this.machineName = machineDesc.getMachineName();
        this.gmtCreate = LocalDateTime.now();
    }
}
