package top.anly.business.machine.enums;

/**
 * 电脑横机的状态
 * @author anlythree
 * @date 2020/11/10 13:46
 */
public enum MachineDescStatusEnum {

    /**
     * 关机状态，运行状态，暂停，报警, 离线
     */
    SHUT_DOWN(0,"shut_down","关机状态"),
    RUNNING(1,"run","运行状态"),
    PENDING(2,"pending","暂停"),
    ERROR(3,"error","报警"),
    NOT_ONLINE(4,"not_online","离线");

    /**
     * 状态码
     */
    private Integer statusNum;

    /**
     * 状态名称
     */
    private String statusCode;

    /**
     * 状态中文说明
     */
    private String statusMessage;

    MachineDescStatusEnum(Integer statusNum, String statusCode, String statusMessage) {
        this.statusNum = statusNum;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }


    /**
     * 根据数字编码（statusNum）查询枚举
     *
     * @param statusNum
     * @return
     */
    public static MachineDescStatusEnum getMsgByCode(Integer statusNum) {
        if (statusNum != null) {
            for (MachineDescStatusEnum one : MachineDescStatusEnum.values()) {
                if (one.getStatusNum().equals(statusNum)) {
                    return one;
                }
            }
        }
        return null;
    }

    public Integer getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(Integer statusNum) {
        this.statusNum = statusNum;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }



}
