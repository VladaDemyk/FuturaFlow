package com.futura.FuturaFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // Додали імпорт

@SpringBootApplication
@EnableScheduling // УВІМКНУЛИ ПЛАНУВАЛЬНИК
public class FuturaFlowApplication {
	public static void main(String[] args) {
		SpringApplication.run(FuturaFlowApplication.class, args);
	}
}