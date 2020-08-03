package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class IsBookAvailable implements Event<Integer> {

  private Customer customer;
  private String Bookname;

    public IsBookAvailable(Customer _customer, String _name)
    {
        customer=_customer;
        Bookname=_name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookName() {
        return Bookname;
    }
}
