package ticketController;

import Exceptions.TicketNotAddedException;
import Exceptions.UserExistsException;
import Exceptions.UserNotAddedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import userController.UserController;
import userController.UserEntry;
import util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDao {
    public static List<TicketEntry> getAllTickets(UserEntry user) throws SQLException {
        List<TicketEntry> entries = new ArrayList<>();
        Connection con = Util.getConnection();
        String selectSql;
        PreparedStatement stmt;
        if(user.isManager()){
            selectSql = "select * from tickets where pending = TRUE";
            stmt = con.prepareStatement(selectSql);
        }else{
            selectSql = "select * from tickets where username = ?";
            stmt = con.prepareStatement(selectSql);
            stmt.setString(1, user.getUsername());
        }
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            entries.add(
                    new TicketEntry(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getBoolean("pending"))
            );
        }
        return entries;
    }

    public static void addTicket(TicketEntry entry) throws SQLException, TicketNotAddedException {
        Connection con = Util.getConnection();
        String insertSql = "insert into tickets(username, amount, description, pending) values(?, ?, ?, true)";
        PreparedStatement stmt;

        //Create a user
        stmt = con.prepareStatement(insertSql);
        stmt.setString(1, entry.getUsername());
        stmt.setDouble(2, entry.getAmount());
        stmt.setString(3, entry.getDescription());
        int i = stmt.executeUpdate();
        if (1 != i) {
            throw new TicketNotAddedException();
        }
    }
}
