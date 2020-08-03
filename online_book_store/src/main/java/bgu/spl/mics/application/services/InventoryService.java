package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.IsBookAvailable;
import bgu.spl.mics.application.messages.BookToTake;
import bgu.spl.mics.application.messages.TerminateBroadcat;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private  static int num = 1;

	public InventoryService() {
		super("inventoryService" + num);
		inventory= Inventory.getInstance();
		num++;
	}

	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */

	protected void initialize() {


		subscribeEvent(IsBookAvailable.class, ev -> {

			int price = inventory.checkAvailabiltyAndGetPrice(ev.getBookName());
			if(price == -1)
			{
				complete(ev,-1);
			}
			else {
				complete(ev, price);
			}
				});

		subscribeEvent(BookToTake.class, message ->{

			if((inventory.take(message.getBookName())).equals(OrderResult.SUCCESSFULLY_TAKEN))
			{
				complete(message,true );
			}
			else
			{
				complete(message,false );
			}

		});


		subscribeBroadcast(TerminateBroadcat.class, message ->{
			this.terminate();

		});


		BookStoreRunner.counter.countDown();
		
	}

}


