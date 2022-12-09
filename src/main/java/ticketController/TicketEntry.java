package ticketController;

public class TicketEntry {
    private long id;
    private String username;
    private double amount;
    private String description;
    private boolean pending;
    private boolean approved;
    public TicketEntry() {
        id = -1;
        amount = -1;
        pending = true;
        approved = false;
    }

    /**
     * Constructor for a TicketEntry object.
     * @param username
     * @param amount
     * @param description
     * @param pending
     */
    public TicketEntry(long id, String username, double amount, String description, boolean pending, boolean approved) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.description = description;
        this.pending = pending;
        this.approved = approved;
    }

    public long getId() { return id;}

    public void setId(long id) { this.id = id; }

    public boolean isApproved() { return approved;  }

    public void setApproved(boolean approved) { this.approved = approved; }

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