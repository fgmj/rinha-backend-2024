package br.com.fernandojunior.rinhaspringdemo;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RinhaSpringDemoApplication  {

	public static void main(String[] args) {
		SpringApplication.run(RinhaSpringDemoApplication.class, args);
	}



}
