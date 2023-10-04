package com.ktdsuniversity.edu.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

// @Component
public class FileHandler {
	
	/**
	 * 업로드된 파일이 저장될 위치
	 */
	private String baseDir;
	
	/**
	 * 파일명을 난독화할지에 대한 여부
	 */
	private boolean enableObfuscation;
	
	/**
	 * 확장자를 숨길지에 대한 여부
	 */
	private boolean enableObfuscationHideExt;

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public void setEnableObfuscation(boolean enableObfuscation) {
		this.enableObfuscation = enableObfuscation;
	}

	public void setEnableObfuscationHideExt(boolean enableObfuscationHideExt) {
		this.enableObfuscationHideExt = enableObfuscationHideExt;
	}
	
	/**
	 * 서버에 등록한 파일 반환
	 * @param fileName 찾아올 파일명
	 * @return 파일 객체
	 */
	public File getStoredFile(String fileName) {
		return new File(baseDir, fileName);
	}
	
	
	/**
	 * 파일 다운로드 처리
	 * @param downloadFile 다운로드 시킬 서버의 파일 객체
	 * @param downloadFileName 브라우저에게 전달할 파일 이름
	 * @return 다운로드 리소스
	 */
	public ResponseEntity<Resource> getResponseEntity(
			File downloadFile,
			String downloadFileName) {
		
		// HTTP 응답의 Header 정보 생성
		HttpHeaders header = new HttpHeaders();
		// HTTP 응답 Header에 파일을 첨부하여 보내도록 설정
		header.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=" + downloadFileName);
		
		// 다운로드할 파일의 리소스(Byte) 생성
		InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(downloadFile)); 
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("파일이 존재하지 않습니다");
			}
		
		// HTTP 응답 객체 직접 생성
		return ResponseEntity.ok() // status = 200
				.headers(header) // HTTP header 설정
				.contentLength(downloadFile.length()) // 다운로드할 파일의 크기
				// 다운로드할 파일의 타입
				// application/octest-straem : 모든 파일
				.contentType(MediaType.parseMediaType("application/octet-strem"))
				//다운로드할 파일의 resource(byte)
				.body(resource);
	}
	
	/**
	 * 사용자가 업로드한 파일을 서버에 저장
	 * @param multipartFile 사용자가 업로드한 파일
	 * @return 업로드 결과
	 */
	public StoredFile storeFile(MultipartFile multipartFile) {
		
		// 사용자가 파일을 업로드하지 않았다면 null 반환
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}
		
		// 사용자가 업로드한 파일의 이름
		String originalFileName = multipartFile.getOriginalFilename();
		// 사용자가 업로드한 파일의 이름을 난독화 설정에 따라 받아옴
		String fileName = getObfuscationFileName(originalFileName);
		// 파일이 저장될 위치 지정
		File storePath = new File(baseDir, fileName);
		// 만약 파일이 저장될 위치(폴더)가 존재하지 않는다면
		if (!storePath.getParentFile().exists()) {
			// 폴더를 생성해준다
			storePath.getParentFile().mkdirs();
		}
		
		// 사용자가 업로드한 파일을 파일이 저장될 위치로 이동
		try {
			multipartFile.transferTo(storePath);			
		} catch (IllegalStateException | IOException e) {
			// 업로드한 파일을 이동하는 중에 예외가 발생하면
			// 업로드를 실패한 것이므로 null 반환
			return null;
		}
		// 업로드 결과 반환
		return new StoredFile(originalFileName, storePath);
	}
	
	/**
	 * 파일명을 난독화 처리하는 기능
	 * @param fileName 사용자가 업로드한 파일의 이름
	 * @return 설정값에 따라, 난독화된 이름 또는 업로드한 파일의 이름
	 */
	private String getObfuscationFileName(String fileName) {
		
		// 난독화 설정을 했을 때
		if (enableObfuscation) {
			// 파일명에서 확장자 분리
			String ext = fileName.substring(fileName.lastIndexOf("."));
			// 현재 시간을 기준으로 난독화된 코드 받아옴
			String obfuscationName = UUID.randomUUID().toString();
			// 확장자를 숨김처리 설정을 했다면
			if (enableObfuscationHideExt) {
				// 확장자를 제외한 난독화된 코드만 반환하고
				return obfuscationName;
			}
			// 확장자를 숨김처리하지 않았다면
			// 난독화된 코드 뒤에 확장자를 붙여서 반환
				return obfuscationName + ext;
			}
		
		return fileName;
	}
	
	// Class 안 Class => Nested Class
	public class StoredFile {
		/**
		 * 사용자가 업로드한 파일의 이름 (확장자 포함)
		 */
		private String fileName;
		
		/**
		 * 서버에 저장된 파일의 실제 이름
		 * 난독화가 설정했다면, 파일의 이름은 난독화되어 저장됨
		 */
		private String realFileName;
		
		/**
		 * 서버에 저장된 파일의 경로 
		 */
		private String realFilePath;
		
		/**
		 * 서버에 저장된 파일의 크기 (byte 단위)
		 */
		private long fileSize;
		
		/**
		 * 업로드한 파일의 정보 셋팅
		 * @param fileName 사용자가 업로드한 파일의 이름
		 * @param storeFile 서버에 저장된 파일 객체
		 */
		StoredFile(String fileName, File storeFile) {
			this.fileName = fileName;
			this.realFileName = storeFile.getName();
			this.realFilePath = storeFile.getAbsolutePath();
			this.fileSize = storeFile.length();
		}

		public String getFileName() {
			return fileName;
		}
		
		public String getRealFileName() {
			return realFileName;
		}
		
		public String getRealFilePath() {
			return realFilePath;
		}
		
		public long getFileSize() {
			return fileSize;
		}
		
	}
}
