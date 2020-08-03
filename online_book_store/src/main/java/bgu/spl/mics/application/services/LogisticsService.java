package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private static int num = 1;
	public LogisticsService() {
		super("logisticService" + num);
		num ++;

	}

	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */
	protected void initialize() {

		subscribeEvent(DeliveryEvent.class, event ->
		{
			AquireVehicle aquireVehicle= new AquireVehicle(event.getAdress(),event.getDistance());
			sendEvent(aquireVehicle);
		});
		subscribeEvent(DeliveryEvent2.class, message ->{

			message.getDeliveryVehicle().deliver(message.getAdress(), message.getDistance());
			complete(message,message.getDeliveryVehicle());
		});
		subscribeBroadcast(TerminateBroadcat.class, message ->{
			this.terminate();
		});
		BookStoreRunner.counter.countDown();
		}

}
