package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.services.APIService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private MessageBus messageBus;
    @Before
    public void setUp() throws Exception {
        messageBus = MessageBusImpl.getInstance();
        MicroService m = new APIService();
//        subscribeEvent( ,m);
    }
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent()
    {

    }

    @Test
    public void getInstance()
    {
        assertNotNull(messageBus);
        MessageBus messageBus2 = MessageBusImpl.getInstance();
        assertEquals(messageBus, messageBus2);
    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }
}