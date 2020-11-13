package top.anly.business.runpart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.anly.business.runpart.domain.RunPart;

/**
 * @author anlythree
 * @date 2020/11/11 10:39
 */
public interface RunPartService extends IService<RunPart> {

    /**
     * 根据机器名称查询未关闭运行时间段
     * @param machineName
     * @return
     */
    RunPart getNotCloseRunPart(String machineName);
}
