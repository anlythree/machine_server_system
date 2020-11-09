package top.anly.business.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wangli
 * @date 2020/11/9 17:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineDesc {

    private Integer id;

    private String machineName;

    private String machineRemark;

    private Integer machineStatus;

    private LocalDateTime machineOpenTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
