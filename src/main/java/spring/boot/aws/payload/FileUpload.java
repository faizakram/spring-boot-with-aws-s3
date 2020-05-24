package spring.boot.aws.payload;

public class FileUpload {
	private String fileName;
	private Long fileSize;
	private String filePath;
	private byte[] file;
	
	
	
	public FileUpload(String fileName, Long fileSize, String filePath) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.filePath = filePath;
	}

	public FileUpload() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

}
