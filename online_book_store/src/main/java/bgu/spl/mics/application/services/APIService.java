package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcat;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.TickAndBookName;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private CopyOnWriteArrayList <TickAndBookName> orderSchedule;
	private ConcurrentHashMap<String,Future<OrderReceipt>> futureList;
	private Customer customer;
	private static int number = 0;

	/**
	 *  constructor
	 * @param _orderSchedule orders and ticks of the customer
	 * @param _customer
	 */
	public APIService(CopyOnWriteArrayList<TickAndBookName> _orderSchedule, Customer _customer) {
		super("APIService" + number);
		number++;
		orderSchedule = new CopyOnWriteArrayList<>();
		for (int i = 0 ; i <_orderSchedule.size() ; i++ ) {
			{
				orderSchedule.add(_orderSchedule.get(i));
			}
			customer = _customer;
			futureList = new ConcurrentHashMap<>();
		}
	}

	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */
	protected void initialize()
	{
		subscribeBroadcast(TickBroadcast.class, message -> {
			for (int i = 0 ; i < orderSchedule.size() ; i ++) {
				if (message.getCurrTick() == orderSchedule.get(i).getTickBook())
				{
					BookOrderEvent event = new BookOrderEvent(customer, orderSchedule.get(i).getBookName(), orderSchedule.get(i).getTickBook());
					Future<OrderReceipt> f = sendEvent(event);
					if (f != null) {
						futureList.put(orderSchedule.get(i).getBookName(), f);
					}
				}
			}
			for (Map.Entry<String, Future<OrderReceipt>> entry : this.futureList.entrySet())
			{
				Future<OrderReceipt> future = entry.getValue();
				OrderReceipt thisRecipt= future.get();
				if(thisRecipt != null) {
					customer.addReceipt(thisRecipt);
					DeliveryEvent deliveryevent = new DeliveryEvent(customer.getAddress(), customer.getDistance());
					sendEvent(deliveryevent);
				}
			}
				futureList.clear();

			});
			subscribeBroadcast(TerminateBroadcat.class, message ->{

				this.terminate();

			});
		BookStoreRunner.counter.countDown();


	}

}
