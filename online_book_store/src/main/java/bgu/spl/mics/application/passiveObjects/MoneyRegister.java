package bgu.spl.mics.application.passiveObjects;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;
import bgu.spl.mics.application.BookStoreRunner;


/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 *  * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private static CopyOnWriteArrayList<OrderReceipt> moneyregisterarray;
	private static MoneyRegister moneyregister_instance = null;
	private int sum =0;
	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance()
	{
		if(moneyregister_instance == null)
		{
			moneyregister_instance = new MoneyRegister ();
			moneyregisterarray = new CopyOnWriteArrayList<>();
		}
		return moneyregister_instance;
	}
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r)
	{
		moneyregisterarray.add(r);
		sum += r.getPrice();
	}
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings()
	{
		return sum;
	}
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount)
	{
		c.setamount(c.getAmount() - amount);
	}
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename)
	{
		BookStoreRunner.outPutPrinter(filename, moneyregisterarray);
	}
	public CopyOnWriteArrayList<OrderReceipt> getOrderReceipts(){
		return moneyregisterarray;
	}
}
