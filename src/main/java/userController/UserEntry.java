package userController;


import java.util.Objects;

public class UserEntry {
    private String username;
    private String password;
    private String session;
    private boolean manager;


    public UserEntry() {
        manager = false;
    }

    public UserEntry(String username, String password, String session, boolean manager) {
        this.username = username;
        this.password = password;
        this.session = session;
        this.manager = manager;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntry)) return false;
        UserEntry userEntry = (UserEntry) o;
        return isManager() == userEntry.isManager() && getUsername().equals(userEntry.getUsername()) && getPassword().equals(userEntry.getPassword()) && Objects.equals(getSession(), userEntry.getSession());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getSession(), isManager());
    }
}