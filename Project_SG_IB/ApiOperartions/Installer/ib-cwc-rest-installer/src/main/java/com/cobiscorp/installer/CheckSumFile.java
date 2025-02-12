/**
 * 
 */
package com.cobiscorp.installer;

/**
 * @author smejia
 *
 */
public class CheckSumFile {
	
	private String fileName;
	private String version;
	private String filePathCTS;
	private String filePathLastVersion;
		
	/**
	 * @param fileName
	 * @param version
	 * @param filePathCTS
	 * @param filePathLastVersion
	 */
	public CheckSumFile(String fileName, String version, String filePathCTS,
			String filePathLastVersion) {
		super();
		this.fileName = fileName;
		this.version = version;
		this.filePathCTS = filePathCTS;
		this.filePathLastVersion = filePathLastVersion;
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the filePathCTS
	 */
	public String getFilePathCTS() {
		return filePathCTS;
	}
	/**
	 * @param filePathCTS the filePathCTS to set
	 */
	public void setFilePathCTS(String filePathCTS) {
		this.filePathCTS = filePathCTS;
	}
	/**
	 * @return the filePathLastVersion
	 */
	public String getFilePathLastVersion() {
		return filePathLastVersion;
	}
	/**
	 * @param filePathLastVersion the filePathLastVersion to set
	 */
	public void setFilePathLastVersion(String filePathLastVersion) {
		this.filePathLastVersion = filePathLastVersion;
	}
}