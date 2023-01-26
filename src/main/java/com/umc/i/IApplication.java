package com.umc.i;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
public class IApplication {

	public static void main(String[] args) {
		SpringApplication.run(IApplication.class, args);

		// 메모리 사용량 출력
        // long heapSize = Runtime.getRuntime().totalMemory();
        // System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");
	}
	@Bean
	public ServerEndpointExporter serverEndpointExporter(){
		return new ServerEndpointExporter();
	}

}
