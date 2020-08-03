package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer customer;
    private int tick;
    private String bookName;

    public BookOrderEvent(Customer _customer, String _name, int _tick)
    {
        customer=_customer;
        tick=_tick;
        bookName=_name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookName() {
        return bookName;
    }

    public int getTick() {
        return tick;
    }
}
