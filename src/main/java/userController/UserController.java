package userController;

import Exceptions.BadUserException;
import Exceptions.UserExistsException;
import Exceptions.UserNotAddedException;
import Exceptions.UsernamePasswordMismatchException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import java.sql.SQLException;
import java.util.Random;
import java.util.regex.Pattern;

public class UserController {
    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    public  void addUser(Context ctx)   {
        try{
            UserEntry entry = validateInput(ctx);
            userDao.addUser(entry);
            ctx.status(201);
        }catch(UserExistsException ex){
            ctx.status(400);
            ctx.result(ex.getMessage());
        }catch(UserNotAddedException | SQLException ex){
            ctx.status(500);
            ctx.result("Internal Error: Database error. User wasn't added");
        }catch(JsonProcessingException | BadUserException ex){
            ctx.status(400);
            ctx.result("Bad format: username and password needed!");
        }
    }

    public  void login(Context ctx){
        try{
            UserEntry entry = validateInput(ctx);
            userDao.findUser(entry);
            if(!ctx.pathParam("uname").equals(entry.getUsername())){
                throw new BadUserException();
            };
            entry.setSession(generateSessionId());
            userDao.setSession(entry);
            ctx.cookie("session", entry.getSession());
            ctx.html("You are in with cookie:" + entry.getSession());
            ctx.status(200);
        }catch(UsernamePasswordMismatchException exp){
            ctx.html("Wrong username or password!");
            ctx.status(401);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }catch(JsonProcessingException | BadUserException ex){
            ctx.status(400);
            ctx.result("Bad format: username and password needed!");
        }
    }

    private  UserEntry validateInput(Context ctx) throws BadUserException, JsonProcessingException{
        UserEntry entry = new ObjectMapper().readValue(ctx.body(), UserEntry.class);
        if(null == entry.getPassword() ||
                null == entry.getUsername() ||
                !Pattern.compile("^(.+)@(\\S+)$")
                        .matcher(entry.getUsername())
                        .matches()){
            throw new BadUserException();
        }
        return entry;
    }

    private String generateSessionId(){
        final int size = 255;
        String alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        char[] newArray = new char[size];
        Random random = new Random();
        for (int i = 0; i < size; i++){
            newArray[i] =   alphaNum.charAt(random.nextInt(alphaNum.length()));
        }
        return new String(newArray);
    }
}
