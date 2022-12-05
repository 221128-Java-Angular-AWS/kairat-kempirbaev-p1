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
            propInput = new FileInputStream(propertiesPath);
            appProps.load(propInput);
            String db_src_url = appProps.getProperty("URL");
            String db_name = appProps.getProperty("DB");
            String db_user = appProps.getProperty("USER");
            String db_pass = appProps.getProperty("PASSWORD");
            Class.forName("org.postgresql.Driver");
            String URL = db_src_url + '/' + db_name;
            Connection conn = DriverManager.getConnection(URL, db_user, db_pass);
            Javalin app = Javalin.create().start(7000);
            app.get("/", ctx -> ctx.result("Hello World"));
            app.get("/login", ctx -> ctx.result("Trying to log in"));
            app.get("/user/:name", ctx -> {
                ctx.result(ctx.pathParam("name") );
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


