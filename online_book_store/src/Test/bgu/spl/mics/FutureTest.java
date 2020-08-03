package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest
{
    private Future <OrderReceipt> temp;
    private OrderReceipt rc = new OrderReceipt(1, "ofir", 2, "mith" , 0,0,0,0);

    @Before
    public void setUp() throws Exception
    {
        temp = new Future<OrderReceipt>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get()
    {
        temp.resolve(rc);
        OrderReceipt rc2 = temp.get();
        assertEquals(rc,rc2);
    }

    @Test
    public void resolve()
    {
        temp.resolve(rc);
        assertTrue(temp.isDone());
    }

    @Test
    public void isDone()
    {
        assertFalse(temp.isDone());
    }

    @Test
    public void get1() {
    }
}