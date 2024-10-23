package Dto;

import java.io.Serializable;

public class FileData implements Serializable {
	private String fileName;
	private long fileSize;
	private byte[] fileContent;

	public FileData(String fileName, long fileSize, byte[] fileContent) {
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileContent = fileContent;
	}

	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public byte[] getFileContent() {
		return fileContent;
	}
}
