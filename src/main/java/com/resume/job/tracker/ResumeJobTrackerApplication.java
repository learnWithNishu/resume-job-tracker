package com.resume.job.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ResumeJobTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeJobTrackerApplication.class, args);
	}

}
