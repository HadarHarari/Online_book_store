package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliveryEvent2 implements Event <DeliveryVehicle> {

    private String adress;
    private int distance;
    private DeliveryVehicle deliveryVehicle;

    public DeliveryEvent2(String _adress, int _distance, DeliveryVehicle _deliveryVehicle)
    {
        adress = _adress;
        distance = _distance;
        deliveryVehicle = _deliveryVehicle;
    }

    public int getDistance()
    {
        return distance;
    }
    public DeliveryVehicle getDeliveryVehicle()
    {
        return deliveryVehicle;
    }

    public String getAdress(){
        return adress;
    }
}
