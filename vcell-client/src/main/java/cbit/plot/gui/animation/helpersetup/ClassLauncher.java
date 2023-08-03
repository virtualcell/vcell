/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassLauncher {
    
    // I want this to return a reference to the thread it launches
    public static Thread start(final String classname, final String...params)
            throws Exception {
        final Class<?> clazz = Class.forName(classname);
        final Method main = clazz.getMethod("main", String[].class);
        
        Thread t = new Thread(new Runnable (){
            @Override
            public void run(){
                try{
                    main.invoke(null, new Object[]{params});
                } catch(IllegalAccessException | IllegalArgumentException 
                        | InvocationTargetException e){
                    throw new AssertionError(e);
                }
            }
        });
        t.start();
        return t;
    }
    
    public static Thread startWithArray(final String classname, final Object [] args)
            throws Exception {
        final Class<?> clazz = Class.forName(classname);
        final Method main = clazz.getMethod("main", String[].class);
        
        Thread t = new Thread(new Runnable (){
            @Override
            public void run(){
                try{
                    main.invoke(null, (Object)args);
                } catch(IllegalAccessException | IllegalArgumentException 
                        | InvocationTargetException e){
                    throw new AssertionError(e);
                }
            }
        });
        t.start();
        return t;
    }
    
}
