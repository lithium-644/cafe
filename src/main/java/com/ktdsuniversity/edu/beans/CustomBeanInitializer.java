package com.ktdsuniversity.edu.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Spring이 직접 Bean으로 만드는 @Component Annotaion이 적용된
 * Class와 함께 수동으로 Bean을 만들도록 하는 클래스
 */
@SpringBootConfiguration
public class CustomBeanInitializer {
	
	/**
	 * @Value는 환경설정파일(application.yml)에서
	 * 설정 값을 찾아, 반환하는 Annotation
	 * 문법: ${키.키.키....: 기본값}
	 */
	@Value("${app.multipart.base-dir:C:/uploadFiles}")
	private String baseDir;
	
	@Value("${app.multipart.obfuscation.enable:false}")
	private boolean enableObfuscation;
	
	@Value("${app.multipart.obfuscation.hide-ext.enable:flase}")
	private boolean enableObfuscationHideExt;
	
	
	/**
	 * @Bean
	 * Bean Container에 적재하도록하는 Annotation
	 * 
	 * Method의 반환타입이 Bean의 타입이 되고
	 * Metohd의 이름이 Bean의 이름이 됨
	 * 
	 * 이 메소드는 반드시 반환되어야 함
	 * @return
	 */
	@Bean
	public FileHandler fileHandler() {
		
		FileHandler fileHandler = new FileHandler();
		fileHandler.setBaseDir(baseDir);
		fileHandler.setEnableObfuscation(enableObfuscation);
		fileHandler.setEnableObfuscationHideExt(enableObfuscationHideExt);
		return fileHandler;
	}
}
