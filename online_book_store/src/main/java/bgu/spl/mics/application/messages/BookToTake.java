package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class BookToTake implements Event <Boolean>{

    private String bookName;

    public BookToTake(String _bookName){
        bookName=_bookName;

    }

    public String  getBookName(){

        return bookName;
    }


}
