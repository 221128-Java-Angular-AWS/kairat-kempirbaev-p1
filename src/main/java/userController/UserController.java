package userController;

import Exceptions.BadInputException;
import Exceptions.UserExistsException;
import Exceptions.UserNotAddedException;
import Exceptions.UsernamePasswordMismatchException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import util.Util;

import java.sql.SQLException;

public class UserController {
    public static void createUser(Context ctx)   {
        try {
            UserEntry entry = UserController.validateInput(ctx);
            UserDao.addUser(entry);
            ctx.status(201);
        }catch(UserExistsException ex){
            ctx.status(400);
            ctx.result("User Exists");
        }catch(UserNotAddedException ex){
            ctx.status(400);
            ctx.result("User wasn't added");
        }catch(SQLException ex){
            ctx.status(500);
            ctx.result("Internal Error");
        }catch(JsonProcessingException ex){
            ctx.status(400);
            ctx.result("Bad format");
        }catch(BadInputException ex){
            ctx.status(400);
            ctx.result("Bad format: username and password needed!");
        }
    }

    public static void login(Context ctx){
        try{
            UserEntry entry = UserController.validateInput(ctx);
            UserDao.findUser(entry);
            entry.setSession(Util.generateSessionId());
            UserDao.setSession(entry);
            ctx.cookie("session", entry.getSession());
            ctx.html("You are in with cookie:" + entry.getSession());
            ctx.status(200);
        }catch(UsernamePasswordMismatchException exp){
            ctx.html("Wrong username or password!");
            ctx.status(401);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }catch(JsonProcessingException ex){
            ctx.status(400);
            ctx.result("Bad format");
        }catch(BadInputException ex){
            ctx.status(400);
            ctx.result("Bad format: username and password needed!");
        }
    }

    private static UserEntry validateInput(Context ctx) throws BadInputException, JsonProcessingException{
        UserEntry entry = new ObjectMapper().readValue(ctx.body(), UserEntry.class);
        if(null == entry.getPassword() || null == entry.getUsername()){
            throw new BadInputException();
        }
        return entry;
    }
}
