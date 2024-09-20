package cbit.vcell.client;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.AsynchClientTaskFunction;
import cbit.vcell.client.task.ClientTaskDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Hashtable;

import static cbit.vcell.client.task.AsynchClientTask.*;

@Disabled
@Tag("Fast")
public class ClientTaskDispatcherTest {
    private final Hashtable<String, Object> ht = new Hashtable<>();
    private final Component cmpt = new Label();

    public ClientTaskDispatcherTest() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void simple() {
        this.test(TASKTYPE_NONSWING_BLOCKING, TASKTYPE_NONSWING_BLOCKING);
    }

    @Test
    public void sbLast() {
        this.test(TASKTYPE_NONSWING_BLOCKING, TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING);
        this.test(TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_BLOCKING, TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING);
    }

    @Test
    public void sbNotLast() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            this.test(TASKTYPE_NONSWING_BLOCKING, TASKTYPE_NONSWING_BLOCKING, TASKTYPE_SWING_NONBLOCKING, TASKTYPE_NONSWING_BLOCKING);
        });
    }

    private AsynchClientTask[] create(int... types) {
        AsynchClientTask[] rval = new AsynchClientTask[types.length];
        int idx = 0;
        for (int t : types) {
            rval[idx] = this.makeTask(idx++, t);
        }
        return rval;
    }

    private void test(int... types) {
        try {
            this.ht.clear();
            AsynchClientTask[] act = this.create(types);
            ClientTaskDispatcher.dispatch(this.cmpt, this.ht, act, false, false, false, null, true);
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
    }

    private AsynchClientTask makeTask(int serial, int taskType) {
        return new AsynchClientTaskFunction(h -> this.mock(serial), "task " + serial, taskType);
    }

    private void mock(int value) {
        System.out.println("mock task " + value);
    }


}
