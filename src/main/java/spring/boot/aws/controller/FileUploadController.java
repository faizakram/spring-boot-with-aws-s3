package spring.boot.aws.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import spring.boot.aws.payload.FileUpload;
import spring.boot.aws.service.FileUploadService;

@RestController
@RequestMapping(value = "/s3")
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@PostMapping(value = "/bucket/create/{bucketName}")
	public String createBucket(@PathVariable String bucketName) {
		return fileUploadService.createBucket(bucketName);
	}

	@GetMapping(value = "/bucket/list")
	public List<String> getBucketList() {
		return fileUploadService.getBucketList();
	}

	@GetMapping(value = "/bucket/files/{bucketName}")
	public List<FileUpload> getBucketfiles(@PathVariable String bucketName) {
		return fileUploadService.getBucketfiles(bucketName);
	}

	@DeleteMapping(value = "/bucket/delete/hard/{bucketName}")
	public String hardDeleteBucket(@PathVariable String bucketName) {
		return fileUploadService.hardDeleteBucket(bucketName);
	}

	@DeleteMapping(value = "/bucket/delete/{bucketName}")
	public String softDeleteBucket(@PathVariable String bucketName) {
		return fileUploadService.softDeleteBucket(bucketName);
	}

	@PostMapping(value = "/file/upload/{bucketName}")
	public String fileUplaod(@PathVariable String bucketName, MultipartFile file) {
		return fileUploadService.fileUplaod(bucketName, file);
	}

	@DeleteMapping(value = "/file/delete/{bucketName}/{fileName}")
	public String deleteFile(@PathVariable String bucketName, @PathVariable String fileName) {
		return fileUploadService.deleteFile(bucketName, fileName);
	}

	@GetMapping(value = "/file/download/{bucketName}/{fileName}")
	public StreamingResponseBody downloadFile(@PathVariable String bucketName, @PathVariable String fileName,
			HttpServletResponse httpResponse) {
		FileUpload downloadFile = fileUploadService.downloadFile(bucketName, fileName);
		httpResponse.setContentType("application/octet-stream");
		httpResponse.setHeader("Content-Disposition",
				String.format("inline; filename=\"%s\"", downloadFile.getFileName()));
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write(downloadFile.getFile());
				outputStream.flush();
			}
		};
	}

}