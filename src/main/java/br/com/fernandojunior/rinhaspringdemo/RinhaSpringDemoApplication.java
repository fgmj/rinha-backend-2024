package br.com.fernandojunior.rinhaspringdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.fernandojunior")
public class RinhaSpringDemoApplication  {

	public static void main(String[] args) {
		SpringApplication.run(RinhaSpringDemoApplication.class, args);
	}


}
