package top.anly.exception;

/**
 * 应用全局错误枚举类
 *
 * @author anlythree
 * @date 2019/4/23 下午1:37
 */
public enum AppError {
    /**
     * 错误码 错误提示
     */
    OK(0, "成功"),
    FAILED(1004, "操作失败"),
    NOLOGIN(7777, "没有登录信息"),
    ERROR(9999, "系统异常"),
    SAVE_FAILED(1004, "保存失败"),
    UPDATE_FAILED(2009,"修改失败"),
    QUERY_FAILED(2010,"查询失败"),
    ;

    private Integer code;
    private String msg;

    AppError(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return code + ":" + msg;
    }

    public Integer getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

}
