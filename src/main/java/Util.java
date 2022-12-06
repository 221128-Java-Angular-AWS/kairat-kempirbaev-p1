import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

public class Util {
    private static Connection conn;

    public static void connect(Properties appProps) throws SQLException, ClassNotFoundException {
        String db_src_url = appProps.getProperty("URL");
        String db_name = appProps.getProperty("DB");
        String db_user = appProps.getProperty("USER");
        String db_pass = appProps.getProperty("PASSWORD");
        Class.forName("org.postgresql.Driver");
        String URL = db_src_url + '/' + db_name;
        Util.conn = DriverManager.getConnection(URL, db_user, db_pass);
    }
    public static void init(){
        try {
            Connection conn = Util.conn;
            //USER TABLE
            PreparedStatement ps = conn.prepareStatement("drop table if exists users;" +
                    "create table users(username varchar(255) PRIMARY KEY, password varchar(255), session varchar(255), manager boolean);" );
            ps.executeUpdate();
            //TICKET TABLE
            ps = conn.prepareStatement("drop table if exists tickets;" +
                    "create table tickets(username varchar(255) references users(username), amount real, description varchar(255), pending boolean);");
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return Util.conn;
    }


    // Use after LOGIN
    public static String generateSessionId(){
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
