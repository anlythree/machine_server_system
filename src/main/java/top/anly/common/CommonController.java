package top.anly.common;

import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.anly.common.mqtt.MqttConnect;

/**
 * @author wangli
 * @date 2020/11/9 17:54
 */
@Slf4j
@ApiModel("debuggerController")
@RequestMapping(produces = "application/json;charset=utf-8", value = "/common", method = RequestMethod.POST)
@RestController
public class CommonController {

    @Autowired
    private MqttConnect mqttConnect;
}
