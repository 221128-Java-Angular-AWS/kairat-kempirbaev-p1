import io.javalin.Javalin;
import ticketController.TicketController;
import ticketController.TicketDao;
import userController.UserController;
import userController.UserDao;
import util.DatabaseManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.sql.*;
public class Main {
    public static void main(String[] args)  {
        String propertiesPath = "app.properties";
        Properties appProps = new Properties();
        try{
            //Get configs
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream ins = loader.getResourceAsStream(propertiesPath);
            appProps.load(ins);

            //Prepare Database
            Connection connection = new DatabaseManager(appProps).getConnection();
            TicketDao ticketDao = new TicketDao(connection);
            UserDao userDao = new UserDao(connection);

            //Initialize controllers
            UserController userController = new UserController(userDao);
            TicketController ticketController = new TicketController(ticketDao, userDao);

            //Init Web-Server(REST)
            Javalin app = Javalin.create().start(7000);
            app.post("/users/{uname}/session", userController::login);
            app.post("/users", userController::addUser);
            app.get("/tickets/{uname}", ticketController::getTickets);
            app.post("/tickets/{uname}", ticketController::submitTicket);
            app.put("/tickets/", ticketController::approveTicket);
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