package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.cobolconverter.demo.CobolToJavaConverter;

@SpringBootApplication
@EnableAutoConfiguration
public class CobolToJavaPocApplication {
	@Autowired
	private static Environment env;
	

	public static void main(String[] args) throws IOException {
		SpringApplication.run(CobolToJavaPocApplication.class, args);
		CobolToJavaConverter.convertCobolToJava();
		
	}
}
