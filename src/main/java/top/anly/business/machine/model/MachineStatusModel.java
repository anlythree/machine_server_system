package top.anly.business.machine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wangli
 * @date 2020/11/11 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineStatusModel {

    private Integer machineStatus;

    private LocalDateTime lastHeartTime;

    /**
     * 通过定义状态直接取当前时间作为最新心跳时间
     * @param machineStatus
     */
    public MachineStatusModel(Integer machineStatus) {
        this.machineStatus = machineStatus;
        this.lastHeartTime = LocalDateTime.now();
    }


}
