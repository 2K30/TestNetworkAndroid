package com._2K30.testnetworkandroid.helper;
/**
 * NetworkHelperException
 * @author 2K30
 *
 */
public class NetworkHelperException extends Exception {

	private String m_message;
	private Exception m_mainException;
	
	public NetworkHelperException(String exception){
		this.m_message = exception;
	}
	
	public NetworkHelperException(String exceptionMessage, Exception mainException){
		this.m_mainException = mainException;
		this.m_message = exceptionMessage;
	}
	
	
	
	public void setMyOwnerException(Exception ex){
		this.m_mainException = ex;
	}
	
	public void setMyMessage(String text){
		this.m_message = text;
	}
	
	public String getMyMessage(){
		return this.m_message;
	}
	
	public Exception getOwnerException(){
		return this.m_mainException;
	}
}
