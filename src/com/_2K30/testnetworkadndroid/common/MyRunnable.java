package com._2K30.testnetworkadndroid.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by konstantin on 14.12.14.
 */
public class MyRunnable implements Runnable {

    private Method m_myMethodToExecute;
    private Object[] m_methodArgs;
    private Object m_receiver;


    /**
     * Creates a runnable instance. with method to execute, receiver(target object=>owner of this method), arguments of this method
     * @param methodToExecuteInRun
     * @param receiver
     * @param args
     */
    public MyRunnable(Method methodToExecuteInRun,Object receiver,Object... args){

        setMethod(methodToExecuteInRun,receiver,args);
    }

    /**
     * Reset defined method
     * @param methodToExecuteInRun Method
     * @param receiver
     * @param args
     */
    private void setMethod(Method methodToExecuteInRun,Object receiver,Object... args){
        this.m_myMethodToExecute = methodToExecuteInRun;
        this.m_methodArgs = args;
        this.m_receiver = receiver;
        this.m_myMethodToExecute.setAccessible(true);
    }

    public void setMyMethod(Method method){
        this.m_myMethodToExecute = method;
        this.m_myMethodToExecute.setAccessible(true);
    }

    public void setMethodArgs(Object[] args){
        this.m_methodArgs = args;
    }

    public void setMethodArguments(Object... argumets){
        this.m_methodArgs = argumets;
    }

    public void setMethodOwnerObject(Object ownerObject){
        this.m_receiver = ownerObject;
    }

    public void resetMethod(Method methodToExecuteInRun,Object executer,Object... args){
        setMethod(methodToExecuteInRun,executer,args);
    }

    @Override
    public void run() {
        try {
            this.m_myMethodToExecute.invoke(this.m_receiver,m_methodArgs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
