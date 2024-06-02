package com.example.domain.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {
		
	@Autowired
	private ResourcePatternResolver resourcePatternResolver;
	
	@Autowired
	private S3Info s3Info;
	
	@Autowired
    private AmazonS3 amazonS3;
	
	private AmazonS3 getS3Client() {
		System.out.println(s3Info.getBucketRegion());
		System.out.println(DefaultAWSCredentialsProviderChain.getInstance());
		return AmazonS3ClientBuilder.standard()
				.withRegion(s3Info.getBucketRegion())
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.build();
	}
	
	@Autowired
	public void setupResolver(ApplicationContext applicationContext,
			 AmazonS3 amazonS3) {
		this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, 
				applicationContext);
	}
	
	// ファイルリスト取得
	public List<Resource> fileList(String fileName) {		
		// 検索ボタン押下時でなければ、「fileName」は空文字
		if (fileName == null) {			
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
		// S3からオブジェクトを取得し、返却
		S3Object s3Object = null;
		try {
			s3Object = amazonS3.getObject(s3Info.getBucketName(), fileName);	
		} catch (Exception e) {
			log.error("S3FileDownloadError", e);
		}                
		return s3Object.getObjectContent();
	}
	
	/** ファイル削除 */
	public void delete(String fileName) {
		try {
			String bucketName = s3Info.getBucketName();
			String key = fileName;
			
			getS3Client().deleteObject(bucketName, key);
		} catch (Exception e) {
			log.error("S3FileDeleteError", e);
		}
	}
}
