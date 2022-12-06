package login;

import java.util.Vector;
import io.javalin.*;
import io.javalin.http.Context;

public class LoginController {
    public static void login(Context ctx){
        ctx.json("login failed");
    }
}
