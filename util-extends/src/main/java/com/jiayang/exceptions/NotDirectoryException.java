package com.jiayang.exceptions;

public class NotDirectoryException extends Exception{
	public NotDirectoryException() {
		super("使用了非路径作为配置项");
	}

}
