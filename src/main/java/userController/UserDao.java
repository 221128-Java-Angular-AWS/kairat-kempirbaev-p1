package userController;

import Exceptions.UserExistsException;
import Exceptions.UserNotAddedException;
import Exceptions.UserSessionExpiredException;
import Exceptions.UsernamePasswordMismatchException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private Connection con;

    public UserDao(Connection con) {
        this.con = con;
    }

    public void addUser(UserEntry entry) throws UserExistsException, UserNotAddedException, SQLException {
        String selectSql = "select username from users where username = ?",
                insertSql = "insert into users(username, password, manager) values(?, ?, ?)";
        PreparedStatement stmt;

        //Check if user exists
        {
            stmt = con.prepareStatement(selectSql);
            stmt.setString(1, entry.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                throw new UserExistsException("User Exists: " + entry.getUsername() + "  is used");
            }
        }

        //Create a user
        stmt = con.prepareStatement(insertSql);
        stmt.setString(1, entry.getUsername());
        stmt.setString(2, entry.getPassword());
        stmt.setBoolean(3, entry.isManager());
        int i = stmt.executeUpdate();
        if (1 != i) {
            throw new UserNotAddedException();
        }
    }

    public  void findUser(UserEntry entry) throws SQLException, UsernamePasswordMismatchException {
        String userSql = "select username from users where username = ? AND password = ?";
        PreparedStatement stmt;

        //Authenticate
        stmt = con.prepareStatement(userSql);
        stmt.setString(1, entry.getUsername());
        stmt.setString(2, entry.getPassword());
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            throw new UsernamePasswordMismatchException();
        }
    }

    public  void setSession(UserEntry entry) throws SQLException {
        String sessionSql = "UPDATE users SET session = ? WHERE username = ?";
        PreparedStatement stmt = con.prepareStatement(sessionSql);
        stmt.setString(1, entry.getSession());
        stmt.setString(2, entry.getUsername());
        stmt.executeUpdate();
    }

    public  UserEntry getSessionUser(String sessionId) throws SQLException, UserSessionExpiredException {
        String sessionSql = "select username, manager from users where session = ?";
        PreparedStatement stmt = con.prepareStatement(sessionSql);
        stmt.setString(1, sessionId);
        ResultSet rs = stmt.executeQuery();
        UserEntry entry = new UserEntry();
        if (rs.next()) {
            entry.setUsername(rs.getString(1));
            entry.setManager(rs.getBoolean(2));
            entry.setSession(sessionId);
        } else {
            throw new UserSessionExpiredException();
        }
        return entry;
    }
}