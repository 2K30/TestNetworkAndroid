package com._2K30.testnetworkadndroid.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by konstantin on 15.12.2014.
 */
public class Common {

    public  static Method[] getMethodFromClass(Class<?> myClass, String methodName){
        int i = 0;
        int size = 0;
        ArrayList<Method> array = new ArrayList<Method>();

        for(Method[] methods = myClass.getDeclaredMethods(); methods.length > 0;){
            Method method = methods[i];
            if(method.getName().equals(methodName)){
                array.add(method);
                break;
            }
            i++;
        }
        return array.toArray(new Method[array.size()]);
    }


    /**
     * Copies ArrayList to Array[]
     * @param lst ArrayList
     * @param myClass Class of target array
     * @param <T> type
     * @return
     */
    public static <T> T[] copyListToArray(ArrayList<T> lst, Class<?> myClass) {

       T[] newArray = (T[]) Array.newInstance(myClass, lst.size());
        System.arraycopy(lst.toArray(), 0, newArray, 0, lst.size());
        return newArray;
    }
}
