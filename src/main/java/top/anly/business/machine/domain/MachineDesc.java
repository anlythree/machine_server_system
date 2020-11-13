package top.anly.business.machine.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author anlythree
 * @date 2020/11/9 17:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineDesc {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private String machineName;

    private String machineRemark;

    private Integer machineStatus;

    private LocalDateTime machineOpenTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
