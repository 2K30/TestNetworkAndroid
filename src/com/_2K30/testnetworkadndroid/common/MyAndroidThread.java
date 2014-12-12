package com._2K30.testnetworkadndroid.common;

import java.lang.Thread.State;


public class MyAndroidThread {

	private Thread m_myThread;
	private Runnable m_myThreadRunnable;
	
	public MyAndroidThread(Runnable runnable){
		m_myThreadRunnable = runnable;
		m_myThread = new Thread(runnable);
	}
	
	/**
     * Starts the new Thread of execution. 
     */
	public synchronized void start() {
		// TODO Auto-generated method stub
		if(m_myThread.getState() == Thread.State.NEW){
			//super.start();
			m_myThread.start();
			String s = "";
			return;
			
		}
		else if(m_myThread.getState() == Thread.State.TERMINATED || m_myThread.getState() == State.RUNNABLE){
			
			m_myThread = new Thread(m_myThreadRunnable);
			m_myThread.start();
		}
		else if(m_myThread.getState() == Thread.State.WAITING){
			//repeat while not terminated.... 
			this.start();
		}
	}
	
	/**
     * Returns the thread's identifier. The ID is a positive <code>long</code>
     * generated on thread creation, is unique to the thread, and doesn't change
     * during the lifetime of the thread; the ID may be reused after the thread
     * has been terminated.
     *
     * @return the thread's ID.
     */
	public long getId() {
		// TODO Auto-generated method stub
		return this.m_myThread.getId();
	}
	
	/**
     * Returns the current state of the Thread. This method is useful for
     * monitoring purposes.
     *
     * @return a {@link State} value.
     */
    public State getState() {
    	return this.m_myThread.getState(); 
    }
	
}
