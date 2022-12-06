import io.javalin.Javalin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.sql.*;
public class Main {
    public static void main(String[] args)  {
        String propertiesPath = "src/main/resources/app.properties";
        Properties appProps = new Properties();
        FileInputStream propInput;
        Connection c = null;
        try{
            //Get configs
            propInput = new FileInputStream(propertiesPath);
            appProps.load(propInput);

            //Prepare Database
            Util.connect(appProps);
            Util.init();

            //Init Web-Server(REST)
            Javalin app = Javalin.create().start(7000);
            app.get("/", ctx -> ctx.result("Hello World"));
            app.get("/login", ctx -> ctx.result("Trying to log in"));
            app.get("/user/{name}", ctx -> {
                ctx.result(ctx.pathParam("name") );
            });
            app.post("/user/register", ctx -> {
                ctx.result("registered" );
            });
            app.get("/admin/approve", ctx -> ctx.result("Trying to approve reimbursement"));
            app.get("/admin/view", ctx -> ctx.result("Get all reimbursements"));
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