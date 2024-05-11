package com.example.domain.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class S3Info {
	
	@Value("${bucket.name}")
	private String bucketName;

}
