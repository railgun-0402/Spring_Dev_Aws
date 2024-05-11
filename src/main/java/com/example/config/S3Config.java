package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	@Bean
	public AmazonS3 amazonS3Client() {
		return AmazonS3ClientBuilder.standard().build();
	}

}
