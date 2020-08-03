package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
    public class BookStoreRunner {
    public static CountDownLatch counter;
    public static void main(String[] args) {
        Gson gson = new Gson();
        int num_of_threads;
        int count = 0;
        JsonParser parser = new JsonParser();
        JsonObject jsob = new JsonObject();

        try {
            jsob = parser.parse(new FileReader(args[0])).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String customers_output = args[1];
        String inventory_output = args[2];
        String order_receipts_output = args[3];
        String money_register_output = args[4];

        // initial inventory
        JsonArray initial_Inventory_Array = jsob.getAsJsonArray("initialInventory");
        BookInventoryInfo[] bookInventoryInfo = gson.fromJson(initial_Inventory_Array, BookInventoryInfo[].class);
        Inventory.getInstance().load(bookInventoryInfo);

        // initial resources
        JsonArray initial_Resources_Array = jsob.getAsJsonArray("initialResources");
        JsonObject temp = initial_Resources_Array.get(0).getAsJsonObject();
        JsonArray temp1 = temp.getAsJsonArray("vehicles");
        DeliveryVehicle[] deliveryVehicles = gson.fromJson(temp1, DeliveryVehicle[].class);
        ResourcesHolder.getInstance().load(deliveryVehicles);


        // initial services
        JsonObject services = jsob.getAsJsonObject("services");

        // inital time service
        JsonObject time = services.getAsJsonObject("time");
        JsonPrimitive obj_speed = time.getAsJsonPrimitive("speed");
        JsonPrimitive obj_duration = time.getAsJsonPrimitive("duration");
        int speed = obj_speed.getAsInt();
        int duration = obj_duration.getAsInt();
        TimeService timeService = new TimeService(speed, duration);
        Thread timer = new Thread(timeService);

        // initial selling
        JsonPrimitive selling = services.getAsJsonPrimitive("selling");
        int selling_num = selling.getAsInt();
        Thread[] sellingServices = new Thread[selling_num];
        for (int i = 0; i < selling_num; i++) {
            SellingService selling_service = new SellingService();
            sellingServices[i] = new Thread(selling_service);
        }

        // initial inventory service
        JsonPrimitive inventoryService = services.getAsJsonPrimitive("inventoryService");
        int inventory_num = inventoryService.getAsInt();
        Thread[] inventoryServices = new Thread[inventory_num];
        for (int i = 0; i < inventory_num; i++) {
            InventoryService inventory_service = new InventoryService();
            inventoryServices[i] = new Thread(inventory_service);
        }

        // initial logistics
        JsonPrimitive logistics = services.getAsJsonPrimitive("logistics");
        int logistics_num = logistics.getAsInt();
        Thread[] logisticsServices = new Thread[logistics_num];
        for (int i = 0; i < logistics_num; i++) {
            LogisticsService logistics_service = new LogisticsService();
            logisticsServices[i] = new Thread(logistics_service);
        }

        // initial resources service
        JsonPrimitive resource = services.getAsJsonPrimitive("resourcesService");
        int resource_num = resource.getAsInt();
        Thread[] resourceServices = new Thread[resource_num];
        for (int i = 0; i < resource_num; i++) {
            ResourceService resource_service = new ResourceService();
            resourceServices[i] = new Thread(resource_service);
        }

        // initial customers
        CopyOnWriteArrayList <TickAndBookName> orderSchedule = new CopyOnWriteArrayList <>();
        HashMap<Integer,Customer> customer_to_print = new HashMap<>();
        JsonArray customers = services.getAsJsonArray("customers");
        Customer[] customers_array = gson.fromJson(customers, Customer[].class);
        Thread[] api_services = new Thread[customers.size()];
        for(int i = 0; i < customers.size() ; i ++ )
        {
            customer_to_print.put(customers_array[i].getId() , customers_array[i]);
            JsonObject tmp = customers.get(i).getAsJsonObject();
            JsonArray order_schedule = tmp.getAsJsonArray("orderSchedule");
            JsonObject creditcard = tmp.getAsJsonObject("creditCard");
            JsonPrimitive obj_number = creditcard.getAsJsonPrimitive("number");
            JsonPrimitive obj_amount = creditcard.getAsJsonPrimitive("amount");
            int number = obj_number.getAsInt();
            int amount = obj_amount.getAsInt();
            customers_array[i].setnumber(number);
            customers_array[i].setamount(amount);
            for(int j = 0 ; j < order_schedule.size() ; j ++ )
            {
                JsonObject obj = order_schedule.get(j).getAsJsonObject();
                JsonPrimitive obj_book_Title = obj.getAsJsonPrimitive("bookTitle");
                JsonPrimitive obj_tick = obj.getAsJsonPrimitive("tick");
                orderSchedule.add(new TickAndBookName(obj_book_Title.getAsString() , obj_tick.getAsInt()));
            }
            APIService apiService = new APIService(orderSchedule , customers_array[i]);
            api_services[i] = new Thread(apiService);
            orderSchedule.clear();

        }
        num_of_threads = 1 + sellingServices.length + inventoryServices.length + logisticsServices.length + resourceServices.length + api_services.length;
        Thread[] thread_array = new Thread[num_of_threads];

        for (int i = 0; i < sellingServices.length; i++) {
            thread_array[count] = sellingServices[i];
            count++;
        }

        for (int i = 0; i < inventoryServices.length; i++) {
            thread_array[count] = inventoryServices[i];
            count++;
        }

        for (int i = 0; i < logisticsServices.length; i++) {
            thread_array[count] = logisticsServices[i];
            count++;
        }

        for (int i = 0; i < resourceServices.length; i++) {
            thread_array[count] = resourceServices[i];
            count++;
        }

        for (int i = 0; i < api_services.length; i++) {
            thread_array[count] = api_services[i];
            count++;
        }

        counter = new CountDownLatch(num_of_threads - 1);

        thread_array[thread_array.length-1] = timer;

        for(int i = 0 ; i < thread_array.length ; i ++)
        {
            thread_array[i].start();
        }


        for (int i = 0 ; i < thread_array.length ; i ++)
        {
            try {
                thread_array[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        OutPutCreator(customer_to_print,customers_output,inventory_output,order_receipts_output,money_register_output);
}
    /**
     *  makes the output
     *
     * @param  customers_array  an absolute URL giving the base location of the image
     * @param  money_register_output the location of the image, relative to the url argument
     * @param customers_output
     * @param inventory_output
     * @param order_receipts_output
     *

     */
    private static void OutPutCreator(HashMap<Integer,Customer> customers_array, String customers_output, String inventory_output, String order_receipts_output, String money_register_output)
    {
        outPutPrinter(customers_output,customers_array); // first output file
        Inventory.getInstance().printInventoryToFile(inventory_output); // second output file
        MoneyRegister.getInstance().printOrderReceipts(order_receipts_output); //third output file
        outPutPrinter(money_register_output,(MoneyRegister.getInstance())); // fourth output file
    }

    /**
     *  method that recieves a filename and a serializable object, and create a file
     * @param filename a given file name
     *
     * @param s serializble object
     */

    public static void outPutPrinter(String filename, Serializable s)
    {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        try {
            fileOutputStream = new FileOutputStream(filename);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(s);
            objectOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

