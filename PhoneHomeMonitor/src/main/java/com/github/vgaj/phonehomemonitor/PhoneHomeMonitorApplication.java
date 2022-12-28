package com.github.vgaj.phonehomemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhoneHomeMonitorApplication
{
	public static void main(String[] args) {
		SpringApplication.run(PhoneHomeMonitorApplication.class, args);
	}
}