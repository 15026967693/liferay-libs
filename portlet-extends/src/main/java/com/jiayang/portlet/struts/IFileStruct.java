package com.jiayang.portlet.struts;

import java.io.File;

public interface IFileStruct {
	public File getFile() ;
	public void setFile(File file) ;
	public String getDownloadName() ;
	public void setDownloadName(String downloadName);
	public String getContentType() ;
	public void setContentType(String contentType);

}
