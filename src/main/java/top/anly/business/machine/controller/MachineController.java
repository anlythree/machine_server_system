package top.anly.business.machine.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.anly.business.machine.service.MachineDescService;


@Api(tags = "机械相关接口")
@Slf4j
@RestController
@RequestMapping(value = "/machine/machine", method = RequestMethod.POST)
public class MachineController {

    @Autowired
    private MachineDescService machineDescService;


}
