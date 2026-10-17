package com._wellness.wellness;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class WellnessApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellnessApplication.class, args);
	}

}
