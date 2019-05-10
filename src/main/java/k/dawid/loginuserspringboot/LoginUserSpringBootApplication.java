package k.dawid.loginuserspringboot;

import k.dawid.loginuserspringboot.controller.AdminController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class LoginUserSpringBootApplication {

    public static void main(String[] args) {
        new File(AdminController.uploadDirectory).mkdir();
        SpringApplication.run(LoginUserSpringBootApplication.class, args);
    }

}
