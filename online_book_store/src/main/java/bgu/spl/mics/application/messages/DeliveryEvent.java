package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class DeliveryEvent implements Event {
    private String adress;
    private int distance;

    public DeliveryEvent(String _adress, int _distance)
    {
        adress=_adress;
        distance=_distance;
    }

    public int getDistance() {
        return distance;
    }

    public String getAdress(){
        return adress;
    }
}

