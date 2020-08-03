package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class AquireVehicle implements Event<Boolean> {
    private String adress;
    private int distance;

    /**
     * constructor
     * @param _adress
     * @param _distance
     */
    public AquireVehicle(String _adress,int _distance)
    {
        adress=_adress;
        distance=_distance;
    }


    /**
     * get the distance
     * @return distance
     */
    public int getDistance()
    {
        return distance;
    }

    /**
     * get adress
     * @return adress
     */
    public String getAdress()
    {
        return adress;
    }
}





