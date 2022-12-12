package ticketController;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketEntry {
    private long id;
    private String username;
    private double amount;
    private String description;
    private boolean approved;
    private Date approvedDate;
    private String approvedDateHumanReadable;
    public TicketEntry() {
        id = -1;
        amount = -1;
        approved = false;
    }

    /**
     * Constructor for a TicketEntry object.
     * @param username
     * @param amount
     * @param description
     */
    public TicketEntry(long id, String username, double amount, String description, boolean approved, Date approvedDate) {
        this.id = id;
        this.username = username;
        this.amount = amount;
        this.description = description;
        this.approved = approved;
        this.approvedDate = approvedDate;
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

    public Date getApprovedDate() { return approvedDate; }

    public void setApprovedDate(Date approvedDate) { this.approvedDate = approvedDate; }

    public String getApprovedDateHumanReadable() {
        if(null != approvedDate)
            return  new SimpleDateFormat("yyyy-MM-dd hh:mm a ").format(approvedDate);
        return "1970 Jan 1";
    }

    public void setApprovedDateHumanReadable(String approvedDateHumanReadable) {
        this.approvedDateHumanReadable = approvedDateHumanReadable;
    }

}