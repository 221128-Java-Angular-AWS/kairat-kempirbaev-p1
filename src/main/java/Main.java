import io.javalin.Javalin;
import ticketController.TicketController;
import userController.UserController;
import util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.sql.*;
public class Main {
    public static void main(String[] args)  {
        String propertiesPath = "app.properties";
        Properties appProps = new Properties();
        Connection c = null;
        try{
            //Get configs
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream ins = loader.getResourceAsStream(propertiesPath);
            appProps.load(ins);

            //Prepare Database
            Util.connect(appProps);
            Util.init();

            //Init Web-Server(REST)
            Javalin app = Javalin.create().start(7000);
            app.get("/", ctx -> ctx.result("Hello World"));
            app.post("/user/login", UserController::login);
            app.post("/user/register", UserController::addUser);
            app.get("/user/ticket", TicketController::getTickets);
            app.post("/user/ticket", TicketController::addTicket);
            app.post("/admin/ticket", TicketController::approveTicket);
        }catch (SQLException e){
            System.out.println("No way to recover, Database error");
            throw new RuntimeException();
        } catch (IOException e) {
            System.out.println("Can't read properties");
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            System.out.println("No psql driver");
            throw new RuntimeException();
        }
    }
}