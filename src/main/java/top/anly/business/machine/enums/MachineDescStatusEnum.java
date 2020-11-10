package top.anly.business.machine.enums;

/**
 * 电脑横机的状态
 * @author wangli
 * @date 2020/11/10 13:46
 */
public enum MachineDescStatusEnum {

    /**
     * 关闭，开始运行，停止，错误
     */
    CLOSE(0,"close"),
    RUN(1,"run"),
    STOP(2,"stop"),
    ERROR(3,"error"),
    DOWN(4,"down");

    /**
     * 状态码
     */
    private Integer statusNum;

    /**
     * 状态名称
     */
    private String statusCode;

    MachineDescStatusEnum(Integer statusNo, String statusCode) {
        this.statusNum = statusNo;
        this.statusCode = statusCode;
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
}
