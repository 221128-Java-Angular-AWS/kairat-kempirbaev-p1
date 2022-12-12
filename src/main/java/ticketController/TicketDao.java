package ticketController;

import Exceptions.TicketNotAddedException;
import userController.UserEntry;
import util.Util;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDao {
    public static long index = 1;
    private static List<TicketEntry>  pendingList = Collections.synchronizedList(new LinkedList());
    public static List<TicketEntry> getAllTickets() {
        return TicketDao.pendingList;
    }

    public static List<TicketEntry> getAllTickets(UserEntry user) throws SQLException {
        List<TicketEntry> entries = new LinkedList<>();
        Connection con = Util.getConnection();
        String selectSql  = "select * from tickets where username = ?";
        PreparedStatement stmt = con.prepareStatement(selectSql);
        stmt.setString(1, user.getUsername());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            entries.add(
                    new TicketEntry(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getBoolean("approved"),
                            rs.getTimestamp("approvedDate")
                    )
            );
        }
        entries.addAll(TicketDao.pendingList.stream()
                                        .filter(ticket -> ticket.getUsername().equals(user.getUsername()))
                                        .collect(Collectors.toList()));
        return entries;
    }

    private static long addTicket() throws SQLException, TicketNotAddedException {
        TicketEntry entry = TicketDao.pendingList.get(0);
        TicketDao.pendingList.remove(0);

        Connection con = Util.getConnection();
        String insertSql = "insert into tickets(username, amount, description, approved) values(?, ?, ?, ?)";
        PreparedStatement stmt;

        //Insert a ticket
        stmt = con.prepareStatement(insertSql);
        stmt.setString(1, entry.getUsername());
        stmt.setDouble(2, entry.getAmount());
        stmt.setString(3, entry.getDescription());
        stmt.setBoolean(4, entry.isApproved());
        int i = stmt.executeUpdate();
        if (1 != i) {
            throw new TicketNotAddedException("Database server error");
        }
        return entry.getId();
    }

    private static void approveFirst(boolean approved) throws TicketNotAddedException {
        if(TicketDao.pendingList.size() == 0){
            throw new TicketNotAddedException("Nothing to add");
        }
        TicketDao.pendingList.get(0).setApproved(approved);
    }

    public static long createTicket(boolean approved) throws TicketNotAddedException, SQLException{
        approveFirst(approved);
        return addTicket();
    }

    public static long submitTicket(TicketEntry entry)  {
        entry.setId(TicketDao.index);
        TicketDao.pendingList.add(entry);
        return TicketDao.index++;
    }
}