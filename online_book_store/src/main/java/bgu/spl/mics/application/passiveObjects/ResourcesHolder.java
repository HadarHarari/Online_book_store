package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.Future;


/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static ResourcesHolder resourcesHolder_instance = null;
	private LinkedBlockingQueue <DeliveryVehicle> vehicles;
	private LinkedBlockingQueue <Future> future_mission;
	private static Object lock = new Object();


	private ResourcesHolder()
	{
		vehicles = new LinkedBlockingQueue<>();
		future_mission = new LinkedBlockingQueue<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {

		if(resourcesHolder_instance == null)
		{
			resourcesHolder_instance = new ResourcesHolder ();
		}
		return resourcesHolder_instance;
		}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle()
	{
		Future<DeliveryVehicle> f = new Future<>();
		synchronized (lock)
		{
			if (!vehicles.isEmpty())
			{
				f.resolve(vehicles.poll());
			}
			else
			{
				future_mission.add(f);
			}
			return f;
		}

	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle)
	{
		synchronized (lock)
		{
			if(!future_mission.isEmpty())
			{
				future_mission.poll().resolve(vehicle);
			}
			else
			{
				vehicles.add(vehicle);
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles)
	{
		if(vehicles !=null)
		{
			for (int i = 0; i < vehicles.length; i++)
			{
				this.vehicles.add(vehicles[i]);
			}
		}
	}
	public LinkedBlockingQueue<DeliveryVehicle> getBookinventoryarray()
	{
		return vehicles;
	}
}
