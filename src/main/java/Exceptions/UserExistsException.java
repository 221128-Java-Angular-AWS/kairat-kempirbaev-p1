package Exceptions;

public class UserExistsException extends Exception{
    public UserExistsException(){
    }

    public UserExistsException(String exc){
        super(exc);
    }
}
