package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister moneyRegister;
	private  static int seller = 1;
	private int currentTick;
	private static int order_id = 0;

	public SellingService() {
		super("sellingService"+ seller);
		moneyRegister=MoneyRegister.getInstance();
		seller++;

	}

	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */
	protected void initialize()
	{
		subscribeBroadcast(TickBroadcast.class,message->
		{
			currentTick = message.getCurrTick();

		});
		subscribeEvent(BookOrderEvent.class, event ->
		{
			IsBookAvailable currBook = new IsBookAvailable(event.getCustomer(),event.getBookName());
           Future<Integer> f = sendEvent(currBook);
           if(f != null)
		   {
           Integer price = f.get();
			Customer customer = event.getCustomer();
           if(price == -1)//if this book is not available
           {
			   complete(event,null);
		   }
		   else {
			   synchronized (customer)
			   {
				   if (customer.getAvailableCreditAmount() >= price)
				   {
					   Future<Boolean> bool = sendEvent(new BookToTake(event.getBookName()));
					   Boolean bool2 = bool.get();
					   if (bool2)
					   {
						   moneyRegister.chargeCreditCard(event.getCustomer(), price);
						   OrderReceipt receipt = new OrderReceipt(order_id, this.getName(), customer.getId(), event.getBookName(), price, currentTick, event.getTick(), currentTick);
						   order_id++;
						   moneyRegister.file(receipt);
						   complete(event, receipt);
					   }
					   else
					   	{
						   complete(event, null);
					   	}
				   }
				   else
				   	{
					   complete(event, null);
				   	}
			   }
		   }
		   }


		});


		subscribeBroadcast(TerminateBroadcat.class, message ->{
			this.terminate();

		});

		BookStoreRunner.counter.countDown();
	}

}
