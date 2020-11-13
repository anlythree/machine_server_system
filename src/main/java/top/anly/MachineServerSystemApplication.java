package top.anly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.anly.business.machine.service.MachineDescService;
import top.anly.business.machine.service.impl.MachineDescServiceImpl;
import top.anly.common.util.SpringContextBeanService;

/**
 * 启动类
 *
 * @author
 * @date
 */
@EnableTransactionManagement
@MapperScan("top.anly.business.*.dao")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class MachineServerSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MachineServerSystemApplication.class, args);
        // 缓存设备信息
        MachineDescService machineDescService = SpringContextBeanService.getBean(MachineDescService.class);
        machineDescService.cacheMachineInfo();
        System.out.println("machine data platform start successfully !!!");
    }

}
