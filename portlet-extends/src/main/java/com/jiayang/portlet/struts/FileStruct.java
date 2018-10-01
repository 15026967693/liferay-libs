package com.jiayang.portlet.struts;

import java.io.File;

import com.liferay.portal.kernel.util.ContentTypes;

public class FileStruct implements IFileStruct{
private File file;
private String downloadName;
private String contentType=ContentTypes.APPLICATION_OCTET_STREAM;
public File getFile() {
	return file;
}
public void setFile(File file) {
	this.file = file;
}
public String getDownloadName() {
	return downloadName;
}
public void setDownloadName(String downloadName) {
	this.downloadName = downloadName;
}
public String getContentType() {
	return contentType;
}
public void setContentType(String contentType) {
	this.contentType = contentType;
}

}
