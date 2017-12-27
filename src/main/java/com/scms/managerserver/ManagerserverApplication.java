package com.scms.managerserver;

import com.scms.managerserver.server.MonitoringServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
@Slf4j
@SpringBootApplication
public class ManagerserverApplication {
	@Resource
	MonitoringServer monitoringServer;

	public static void main(String[] args) {
		SpringApplication.run(ManagerserverApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			try {
				monitoringServer.start();
			} catch (Exception e) {
				log.error("netty Server start failed " + e);
			}
		};
	}

}
