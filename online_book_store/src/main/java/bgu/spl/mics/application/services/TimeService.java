package bgu.spl.mics.application.services;
import java.util.Timer;
import java.util.TimerTask;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.TerminateBroadcat;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
    private int time;
    private Object lock = new Object();

	public TimeService(int speed, int duration) {
		super("Time_Serivce_Name");
		this.speed = speed;
		this.duration = duration;
		this.time=1;

	}
	/**
	 * initilaize the micro service and subscribe him for getting his suitable messages
	 */
	protected void initialize()
	{

		try {
			BookStoreRunner.counter.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		subscribeBroadcast(TerminateBroadcat .class, message ->{
			this.terminate();
		});

		TimerTask timerTask=new TimerTask() {
			@Override
			public void run() {
				if(time <= duration) {
					sendBroadcast(new TickBroadcast(time));
					System.out.println(time);
					time++;
				}
				else
				{
					synchronized (lock)
					{
                     sendBroadcast(new TerminateBroadcat());
                     lock.notifyAll();
                     cancel();
					}
				}
			}
		};

		Timer timer=new Timer();
		// needs to be changed to speed
		timer.scheduleAtFixedRate(timerTask,0,duration);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			timer.cancel();
		}
	}

}
