package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AquireVehicle;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent2;
import bgu.spl.mics.application.messages.TerminateBroadcat;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {

	private ResourcesHolder resourcesHolder;
	private static int num = 1;


	public ResourceService() {
		super("Change_This_Name" + num);
		resourcesHolder = ResourcesHolder.getInstance();
		num ++;

	}

	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */
	protected void initialize()
	{
		subscribeEvent(AquireVehicle.class, event ->
		{
			Future <DeliveryVehicle> futurevehicle = resourcesHolder.acquireVehicle();
			DeliveryVehicle deliveryvehicle= futurevehicle.get();
			complete(event,true);
			Future<DeliveryVehicle> futurevehicle2 = sendEvent(new DeliveryEvent2(event.getAdress(), event.getDistance(), deliveryvehicle));
			if (futurevehicle2 != null)
			{
				DeliveryVehicle deliveryVehicle2 = futurevehicle2.get();
			}
			resourcesHolder.releaseVehicle(deliveryvehicle);
		});

		subscribeBroadcast(TerminateBroadcat.class, message ->{
			this.terminate();

		});
		BookStoreRunner.counter.countDown();
	}


}