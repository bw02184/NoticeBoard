package sws.NoticeBoard.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileRestController {

	/**
	 * 에디터 내 사진 파일 업로드
	 *
	 * @param uploadFile
	 * @return savePath - 저장경로
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadTestPOST(MultipartFile[] uploadFile) {

		String savePath;

		// OS 따라 구분자 분리
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			savePath = System.getProperty("user.dir") + "\\files\\image";
		} else {
			savePath = System.getProperty("user.dir") + "/files/image";
		}

		java.io.File uploadPath = new java.io.File(savePath);

		// 파일 저장 경로가 없으면 신규 생성
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}

		for (MultipartFile multipartFile : uploadFile) {

			String uploadFileName = multipartFile.getOriginalFilename();
			String extension = StringUtils.getFilenameExtension(uploadFileName);

			String uuid = UUID.randomUUID().toString();

			// 파일명 저장
			uploadFileName = uuid + "." + extension;

			java.io.File saveFile = new java.io.File(uploadPath, uploadFileName);

			try {
				multipartFile.transferTo(saveFile);
				return uploadFileName;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return savePath;
	}

	/**
	 * 에디터 내 사진 파일 첨부
	 *
	 * @param fileName
	 * @return
	 */
	@ResponseBody
	@GetMapping(value = "/display")
	public ResponseEntity<byte[]> showImageGET(
		@RequestParam("fileName") String fileName
	) {

		String savePath;

		// OS 따라 구분자 분리
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			savePath = System.getProperty("user.dir") + "\\files\\image\\";
		} else {
			savePath = System.getProperty("user.dir") + "/files/image/";
		}

		// 설정한 경로로 파일 다운로드
		java.io.File file = new java.io.File(savePath + fileName);

		ResponseEntity<byte[]> result = null;

		try {

			HttpHeaders header = new HttpHeaders();
			header.add("Content-type", Files.probeContentType(file.toPath()));

			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

		} catch (NoSuchFileException e) {
			log.error("No Such FileException {}", e.getFile());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/uploadBase64", method = RequestMethod.POST)
	public String handleBase64Upload(@RequestBody String base64Image) {
		try {
			int maxLength = 20;

			String filename = truncateAndAppendTimestamp(base64Image, maxLength) + ".png";
			String savePath;
			String filePath;

			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				savePath = System.getProperty("user.dir") + "\\files\\image";
				filePath = savePath + "\\" + filename;
			} else {
				savePath = System.getProperty("user.dir") + "/files/image";
				filePath = savePath + "/" + filename;
			}

			if (!new java.io.File(savePath).exists()) {
				try {
					new java.io.File(savePath).mkdir();
				} catch (Exception e) {
					e.getStackTrace();
				}
			}

			java.io.File file = new File(filePath);

			// BASE64를 일반 파일로 변환하고 저장합니다.
			java.util.Base64.Decoder decoder = Base64.getMimeDecoder();
			byte[] decodedBytes = decoder.decode(base64Image.getBytes());
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(decodedBytes);
			fileOutputStream.close();

			return filename;
		} catch (IOException e) {
			log.error(e.getMessage());

			return "File upload failed.";
		}
	}

	public static String truncateAndAppendTimestamp(String base64Image, int maxLength) {
		// 제거할 특수문자 정규식
		String specialCharactersRegex = "[^a-zA-Z0-9]";

		String truncatedBase64Image = base64Image.length() > maxLength
			? base64Image.substring(base64Image.length() - maxLength)
			: base64Image;

		// 특수문자를 제거하고 timestamp 생성
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		timestamp = timestamp.replaceAll(specialCharactersRegex, "");

		// 특수문자를 제거한 timestamp를 포함하여 결과 문자열 생성
		return new StringJoiner("_")
			.add(truncatedBase64Image.replaceAll(specialCharactersRegex, ""))
			.add(timestamp)
			.toString();
	}
}