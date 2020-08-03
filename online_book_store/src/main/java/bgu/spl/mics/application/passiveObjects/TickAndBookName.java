package bgu.spl.mics.application.passiveObjects;



public class TickAndBookName {
    private String bookName;
    private int tickBook;


    public TickAndBookName(String _bookName,int _tickBook)
    {
        bookName=_bookName;
        tickBook=_tickBook;

    }

    public int getTickBook() {
        return tickBook;
    }

    public String getBookName() {
        return bookName;
    }
}
