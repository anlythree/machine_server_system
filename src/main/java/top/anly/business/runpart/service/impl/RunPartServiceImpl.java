package top.anly.business.runpart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.anly.business.runpart.dao.RunPartDao;
import top.anly.business.runpart.domain.RunPart;
import top.anly.business.runpart.service.RunPartService;

/**
 * @author anlythree
 * @date 2020/11/11 10:39
 */
@Service
public class RunPartServiceImpl extends ServiceImpl<RunPartDao, RunPart> implements RunPartService {


    @Override
    public RunPart getNotCloseRunPart(String machineName) {
        LambdaQueryWrapper<RunPart> runPartLambdaQueryWrapper = Wrappers.lambdaQuery();
        runPartLambdaQueryWrapper.eq(RunPart::getMachineName,machineName)
                .isNull(RunPart::getEndTime)
                .orderByDesc(RunPart::getStartTime)
                .last("limit 1;");
        return getOne(runPartLambdaQueryWrapper);
    }
}
