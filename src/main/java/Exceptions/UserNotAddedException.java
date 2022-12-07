package Exceptions;

public class UserNotAddedException  extends Exception{
    public UserNotAddedException(){
    }

    public UserNotAddedException(String exc){
        super(exc);
    }
}
