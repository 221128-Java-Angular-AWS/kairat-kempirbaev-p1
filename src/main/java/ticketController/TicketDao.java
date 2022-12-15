package ticketController;

import Exceptions.TicketNotAddedException;
import userController.UserEntry;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDao {
    public static long index = 1;
    private  LinkedList<TicketEntry>  pendingList;
    private Connection con;

    public TicketDao(Connection con) {
        this.con = con;
        pendingList = new LinkedList();
    }

    public Connection getConnection(){return con;}

    public  List<TicketEntry> getAllTickets() {
        return pendingList;
    }

    public  List<TicketEntry> getAllTickets(UserEntry user) throws SQLException {
        List<TicketEntry> entries = new LinkedList<>();
        Connection con = getConnection();
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
        entries.addAll(pendingList.stream()
                                        .filter(ticket -> ticket.getUsername().equals(user.getUsername()))
                                        .collect(Collectors.toList()));
        return entries;
    }

    private  long addTicket(boolean approved) throws SQLException, TicketNotAddedException {
        TicketEntry entry;

        //Get an element from the queue
        synchronized(pendingList){
            if(0 < pendingList.size()){
                entry = pendingList.removeFirst();
            }else{
                throw new TicketNotAddedException("No pending tickets");
            }
        }

        entry.setApproved(approved);
        Connection con = getConnection();
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
            throw new RuntimeException("Database server error");
        }
        return entry.getId();
    }

    public  long createTicket(boolean approved) throws TicketNotAddedException, SQLException{
        return addTicket(approved);
    }

    public  long submitTicket(TicketEntry entry)  {
        long id;
        synchronized (pendingList){
            id = TicketDao.index;
            entry.setId(TicketDao.index++);
            pendingList.add(entry);
        }

        return id;
    }
}