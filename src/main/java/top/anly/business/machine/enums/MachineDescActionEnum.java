package top.anly.business.machine.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 电脑横机各动作定义
 * @author anlythree
 * @date 2020/11/12 11:39
 */
@NoArgsConstructor
@AllArgsConstructor
public enum MachineDescActionEnum {

    /**
     * 各动作定义
     */
    OPEN_MACHINE(0,"开机",0,1),
    CLOSE_MACHINE(1,"关机",2,0),
    START_RUN(2,"开始运行",2,1),
    STOP_RUN(3,"停止运行",1,2),
    START_ERROR(4,"开始报警",2,3),
    STOP_ERROR(5,"结束报警",3,2),
    ON_LINE(6,"上线",4,0),
    OFF_LINE(7,"下线",0,4),
    ;


    /**
     * 动作代码
     */
    private Integer actionNum;

    /**
     * 动作内容
     */
    private String actionMessage;

    /**
     * 起始状态
     */
    private Integer startStatusNum;

    /**
     * 结束状态
     */
    private Integer endStatusNum;




    public Integer getActionNum() {
        return actionNum;
    }

    public void setActionNum(Integer actionNum) {
        this.actionNum = actionNum;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }
}
