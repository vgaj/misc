package com.github.vgaj.phonehomemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhoneHomeMonitorApplication
{
	// TODO: Check if this can be built from a clean fetch
	public static void main(String[] args)
	{
		SpringApplication.run(PhoneHomeMonitorApplication.class, args);
	}
}