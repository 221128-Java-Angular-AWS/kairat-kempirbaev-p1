package ticketController;

public class TicketEntry {
    private int id;
    private String username;
    private double amount;
    private String description;
    private boolean pending;
    public TicketEntry() {
        amount = -1;
        pending = true;
    }

    /**
     * Constructor for a TicketEntry object.
     * @param username
     * @param amount
     * @param description
     * @param pending
     */
    public TicketEntry(int id, String username, double amount, String description, boolean pending) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.description = description;
        this.pending = pending;
    }

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}