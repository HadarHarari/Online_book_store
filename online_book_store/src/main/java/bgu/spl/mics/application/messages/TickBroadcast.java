package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

     private int currTick;
    public TickBroadcast(int _currTick)
    {
        currTick =_currTick;
    }


    public int getCurrTick() {
        return currTick;
    }
}
