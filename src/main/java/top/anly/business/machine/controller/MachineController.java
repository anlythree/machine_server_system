package top.anly.business.machine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.anly.business.machine.domain.MachineDesc;
import top.anly.business.machine.model.MachineStatusModel;
import top.anly.business.machine.service.MachineDescService;
import top.anly.common.util.Result;

import java.util.List;
import java.util.Map;


@Api(tags = "机器设备相关接口")
@Slf4j
@RestController
@RequestMapping(value = "/machine/machine", method = RequestMethod.POST)
public class MachineController {

    @Autowired
    private MachineDescService machineDescService;

    /**
     * 查询所有机器设备信息
     *
     * @param
     * @return
     */
    @ApiOperation("查询所有机器设备信息")
    @RequestMapping("/queryMachineInfo")
    @ResponseBody
    public Result<List<MachineDesc>> queryMachineInfo() {
        return Result.ok(machineDescService.queryMachineInfo());
    }

    /**
     * 查询所有机器设备信息
     *
     * @param
     * @return
     */
    @ApiOperation("查询所有机器设备缓存信息")
    @RequestMapping("/queryMachineInfoCache")
    @ResponseBody
    public Result<Map<String, MachineStatusModel>> queryMachineInfoCache() {
        return Result.ok(machineDescService.queryMachineInfoCache());
    }

    /**
     * 手动缓存设备信息
     *
     * @param
     * @return
     */
    @ApiOperation("手动缓存设备信息")
    @RequestMapping("/manualCacheMachineInfo")
    @ResponseBody
    public Result<Map<String, MachineStatusModel>> manualCacheMachineInfo() {
        return Result.ok(machineDescService.manualCacheMachineInfo());
    }

}
