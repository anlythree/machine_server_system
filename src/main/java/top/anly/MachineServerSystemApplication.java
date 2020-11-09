package top.anly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
public class MachineServerSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MachineServerSystemApplication.class, args);
        System.out.println("hj项目启动成功！！！");
    }

}
