package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.application.BookStoreRunner;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory
{

	private CopyOnWriteArrayList<BookInventoryInfo> bookinventoryarray;
	private static Inventory inventory_instance = null;
	Object lock = new Object();

	private Inventory ()
	{
		bookinventoryarray = new CopyOnWriteArrayList<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		if(inventory_instance == null)
		{
			inventory_instance = new Inventory ();
		}
		return inventory_instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */



	public void load (BookInventoryInfo[ ] inventory )
	{
		if(inventory !=null)
		{

			for (int i = 0; i < inventory.length; i++)
			{
				bookinventoryarray.add(inventory[i]);
			}

		}
	}
	public int getSize()
	{
		return bookinventoryarray.size();
	}
	public BookInventoryInfo getBook(int i)
	{
		return bookinventoryarray.get(i);
	}
	public BookInventoryInfo getBook(String book)
	{
		for(int i = 0 ; i < bookinventoryarray.size() ; i ++)
			if(bookinventoryarray.get(i).getBookTitle().compareTo(book) == 0)
			return bookinventoryarray.get(i);
			return null;
	}
	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book)
	{
			synchronized (lock) {
					if (checkAvailabiltyAndGetPrice(book) != -1) {
							inventory_instance.getBook(book).setAmount(inventory_instance.getBook(book).getAmount() - 1);
							return OrderResult.SUCCESSFULLY_TAKEN;
						}
				}
		return OrderResult.NOT_IN_STOCK;
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book)
	{
		for(int i = 0 ; i < bookinventoryarray.size() ; i ++)
		{
				if (bookinventoryarray.get(i).getBookTitle().equals(book) && bookinventoryarray.get(i).getAmount() > 0) {
					return bookinventoryarray.get(i).getPrice();
				}
		}
		return -1;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename)
	{
		HashMap<String, Integer> temp = new HashMap<>();
		for (int i = 0 ; i < bookinventoryarray.size() ; i ++)
		temp.put(bookinventoryarray.get(i).getBookTitle() , bookinventoryarray.get(i).getAmount());
		BookStoreRunner.outPutPrinter(filename, temp);
	}

}
