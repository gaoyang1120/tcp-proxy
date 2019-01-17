package com.blueline.tool.proxy.tcp.domain.machine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class BaseException extends RuntimeException {
	public static final Logger logger = LoggerFactory.getLogger(BaseException.class);
	public BaseException() {
		super();
	}

	public BaseException(String string) {
		super(string);
		logger.error(string);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
		logger.error(getStackTrace(cause));
	}

	public BaseException(Throwable cause) {
		super(cause);
		logger.error(getStackTrace(cause));
	}
	public static String getStackTrace(Throwable e){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(baos));
		String exception = baos.toString();
		if(baos!=null){
			try {
				baos.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
		return exception;
	}
}
