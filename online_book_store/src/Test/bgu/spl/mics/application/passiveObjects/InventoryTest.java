package bgu.spl.mics.application.passiveObjects;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory;
    BookInventoryInfo b = new BookInventoryInfo("yo", 1, 2);
    BookInventoryInfo b1 = new BookInventoryInfo("yo1", 2, 3);
    BookInventoryInfo b2 = new BookInventoryInfo("yo2", 4, 5);
    BookInventoryInfo [] arraybook = {b,b1,b2};

    @Before
    public void setUp() throws Exception {
        inventory = Inventory.getInstance();


    }
    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getInstance() {
        assertNotNull(inventory);
        Inventory inventory2 = Inventory.getInstance();
        assertEquals(inventory, inventory2);
    }

    @Test
    public void load()
    {
        inventory.load(arraybook);
        assertEquals(inventory.getSize(), arraybook.length);
    }

    @Test
    public void take() {
        int a = inventory.getBook(2).getAmountInInventory();
        assertEquals(inventory.take("yo3"), OrderResult.NOT_IN_STOCK);
        assertEquals(inventory.take("yo2"), OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(inventory.getBook(2).getAmountInInventory(),a-1);
        assertTrue(inventory.getBook(2).getAmountInInventory() >= 0);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        int a = inventory.getBook(2).getPrice();
        assertEquals(inventory.checkAvailabiltyAndGetPrice("yo2"),a);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("yo3"),-1);
    }

    @Test
    public void printInventoryToFile() {
    }
}