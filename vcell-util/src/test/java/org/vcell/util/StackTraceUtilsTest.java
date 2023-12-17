package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import java.io.IOException;

import static org.junit.Assert.*;

@Category(Fast.class)
@Tag("Fast")
public class StackTraceUtilsTest {

    private void eenie(){
        meenie();
    }

    private void meenie(){
        miney();
    }

    private void miney(){
        mo();
    }

    private void mo(){
        String stackTraceString = StackTraceUtils.getStackTrace();
        assertTrue(stackTraceString.contains("StackTraceUtilsTest.mo"));
        assertTrue(stackTraceString.contains("StackTraceUtilsTest.miney"));
        assertTrue(stackTraceString.contains("StackTraceUtilsTest.meenie"));
        assertTrue(stackTraceString.contains("StackTraceUtilsTest.eenie"));
        throw new RuntimeException("Hello from Mo!");
    }

    private void layer1() throws IndexOutOfBoundsException{
        try {
            layer2();
        } catch(Exception e){
            throw new RuntimeException("Dummy exception 1", e);
        }
    }

    private void layer2() throws IOException{
        try {
            layer3();
        } catch(Exception e){
            throw new IOException("Dummy exception 2", e);
        }
    }

    private void layer3() throws AuthenticationException{
        throw new ArithmeticException("Dummy exception 3");
    }

    @Test
    public void getStackTraceTest(){
        try {
            eenie();
        } catch(RuntimeException e){
            String stackTraceString = StackTraceUtils.getStackTrace(e);
            assertTrue(stackTraceString.contains("RuntimeException"));
            assertTrue(stackTraceString.contains("Hello from Mo!"));
            assertTrue(stackTraceString.contains("StackTraceUtilsTest.mo"));
            assertTrue(stackTraceString.contains("StackTraceUtilsTest.miney"));
            assertTrue(stackTraceString.contains("StackTraceUtilsTest.meenie"));
            assertTrue(stackTraceString.contains("StackTraceUtilsTest.eenie"));
            return;
        }
        throw new AssertionError("An exception should have been thrown, caught, and returned from!");
    }

    @Test
    public void getCausalChainTest(){
        try {
            layer1();
        } catch(Exception e){
            String causalChainString = StackTraceUtils.getCausalChain(e);
            assertEquals("Dummy exception 1 caused by Dummy exception 2 caused by Dummy exception 3", causalChainString);
        }
    }
}
