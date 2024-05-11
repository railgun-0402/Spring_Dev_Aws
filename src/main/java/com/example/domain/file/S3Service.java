package com.example.domain.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private ResourcePatternResolver resourcePatternResolver;
	
	@Autowired
	private S3Info s3Info;
	
	@Autowired
    private AmazonS3 amazonS3;
	
	@Autowired
	public void setupResolver(ApplicationContext applicationContext,
			 AmazonS3 amazonS3) {
		this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, 
				applicationContext);
	}
	
	// ファイルリスト取得
	public List<Resource> fileList(String fileName) {
		System.out.println(fileName);
		
		if (fileName == null) {
			System.out.println("nullだぞ");
			fileName = "";
		}
		
		List<Resource> resourceList = null;
		try {
			Resource[] resourceArray = resourcePatternResolver
					.getResources("s3://" + s3Info.getBucketName()
					+ "/**/*" + fileName + "*.*");					
			resourceList = Arrays.asList(resourceArray);
		} catch (IOException e) {
			log.error("S3FileListError", e);
		}		
		return resourceList;
	}
	
	/** ファイルアップロード */
	public void upload(MultipartFile uploadFile) {
		Resource resource = this.resourceLoader
				.getResource("s3://" + s3Info.getBucketName()
				+ "/" + uploadFile.getOriginalFilename());

		String fileName = UUID.randomUUID().toString();		

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(uploadFile.getSize());
		metadata.setContentType(uploadFile.getContentType());

        try {
			amazonS3.putObject(new PutObjectRequest(s3Info.getBucketName(), uploadFile.getOriginalFilename(), uploadFile.getInputStream(), metadata));
		} catch (IOException e) {
			log.error("S3FileUploadError", e);
		}
	}
	
	/** ファイルダウンロード */
	public InputStream download(String fileName) throws IOException {
		Resource resource = this.resourceLoader
				.getResource("s3://" + s3Info.getBucketName() + "/" + fileName);

		return resource.getInputStream();
	}
}
