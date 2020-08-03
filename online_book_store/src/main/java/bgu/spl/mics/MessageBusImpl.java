package bgu.spl.mics;
import java.lang.Object;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl message_busimpl = null;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microservicesqueue;
	private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> roundrobin;
	private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> broadcastlist;
	private ConcurrentHashMap<Message, Future> eventfuture;

	private final Object lockA = new Object();
	private final Object lockB = new Object();
	private final Object lockC = new Object();
	private final Object lockD = new Object();
	private final Object lockE = new Object();
	private final Object lockF = new Object();

	/**
	 * constructor
	 */

	private MessageBusImpl() {
		microservicesqueue = new ConcurrentHashMap<>();
		roundrobin = new ConcurrentHashMap<>();
		broadcastlist = new ConcurrentHashMap<>();
		eventfuture = new ConcurrentHashMap<>();
	}

	/**
	 * subscribe a given event and micro service
	 * @param type The type to subscribe of the event
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (lockD) {
			if (!roundrobin.containsKey(type)) {
				LinkedBlockingQueue<MicroService> q = new LinkedBlockingQueue<>();
				roundrobin.put(type, q);
			}
		}
				roundrobin.get(type).add(m);



	}

	/**
	 * create instance of message bus
	 * @return message bus object
	 */
	public static MessageBusImpl getInstance() {
		if (message_busimpl == null) {
			message_busimpl = new MessageBusImpl();
		}
		return message_busimpl;
	}

	/**
	 * subscribe broadcast
	 * @param type 	The type broadcast to subscribe
	 * @param m    	The subscribing micro-service.
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
//		System.out.println("subscribeBroadcast1");
		synchronized (lockE) {
			if (!broadcastlist.containsKey(type)) {
				LinkedBlockingQueue<MicroService> q = new LinkedBlockingQueue<>();
				broadcastlist.put(type, q);
			}
		}
		broadcastlist.get(type).add(m);
	}

	/**
	 * complete the mission and resolve it
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	public <T> void complete(Event<T> e, T result) {
		eventfuture.get(e).resolve(result);
		eventfuture.remove(e);
	}

	/**
	 * add the broadcast to the suitable micro service
	 * @param b 	The broadcast to added to the queues.
	 */
	public void sendBroadcast(Broadcast b) {
//		System.out.println("sendBroadcast1");
		synchronized (lockF)
		{
			if (broadcastlist.get(b.getClass()) != null) {

				for (MicroService m : broadcastlist.get(b.getClass())) {
					microservicesqueue.get(m).offer(b);
				}
//			System.out.println("sendBroadcast2");
			}
		}
	}

	/**
	 *  send event to the suitable micro services
	 * @param e     	The event to add to the  micro service queue messages.
	 * @param <T>
	 * @return future
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		if (!roundrobin.containsKey(e.getClass())) {
			return null;

		} else {
			synchronized (lockC) {
				try {
//					System.out.println("sendEvent3");
					MicroService temp = roundrobin.get(e.getClass()).take();
					roundrobin.get(e.getClass()).add(temp);
					microservicesqueue.get(temp).add(e);
					eventfuture.put(e, f);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return f;
	}

	/**
	 * register the micro service to his suitable messages
	 * @param m the micro-service to register.
	 */
	public void register(MicroService m) {
		LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<>();
		microservicesqueue.put(m, q);
	}

	/**
	 * unregister the micro service
	 * @param m the micro-service to unregister.
	 */
	public void unregister(MicroService m) {
		if (microservicesqueue.containsKey(m))
		{
			LinkedBlockingQueue q = microservicesqueue.get(m);
			synchronized (lockA) {
				microservicesqueue.remove(microservicesqueue.get(m));
				for (Map.Entry<Class, LinkedBlockingQueue<MicroService>> entry : this.roundrobin.entrySet()) {
					LinkedBlockingQueue q1 = entry.getValue();
					q1.remove(m);//delete m from roundrubin in each type event
					if (q1.isEmpty()) {
						roundrobin.remove(entry.getKey());
					}
				}
				}
			while(!q.isEmpty())
				complete((Event)(q.poll()),null);
			}
			synchronized (lockB) {
				for (Map.Entry<Class, LinkedBlockingQueue<MicroService>> entry : this.broadcastlist.entrySet()) {
					LinkedBlockingQueue q = entry.getValue();
					q.remove(m);//delete m from broadcastlist in each type event
					if (q.isEmpty())
					{
						broadcastlist.remove(entry.getKey());
					}
				}
			}
		}

	/**
	 * take the suitable events
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return meesage
	 * @throws InterruptedException
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException {
//		System.out.println("awaitMessage1");
		if (!microservicesqueue.containsKey(m)) {
//			System.out.println("awaitMessage2");
			throw new IllegalStateException();

		}
//		System.out.println("awaitMessage3");
		return microservicesqueue.get(m).take();
	}
}

