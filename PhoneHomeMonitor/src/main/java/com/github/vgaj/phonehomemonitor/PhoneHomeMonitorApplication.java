package com.github.vgaj.phonehomemonitor;

import com.github.vgaj.phonehomemonitor.logic.MonitorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhoneHomeMonitorApplication {
	// TODO: Create RPM

	// TODO: Create Systemd service file

	@Autowired
	private MonitorTask monitorTask;

	public static void main(String[] args) {
		SpringApplication.run(PhoneHomeMonitorApplication.class, args);
	}
}