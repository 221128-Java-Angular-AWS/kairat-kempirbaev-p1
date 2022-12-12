package ticketController;

import Exceptions.BadTicketException;
import Exceptions.TicketNotAddedException;
import Exceptions.UnauthorizedException;
import Exceptions.UserSessionExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import userController.UserDao;
import userController.UserEntry;

import java.sql.SQLException;
import java.util.List;

public class TicketController {
    public static void getTickets(Context ctx) {
        try {
            UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
            List<TicketEntry> entries;
            if (userEntry.isManager()){
                entries = TicketDao.getAllTickets();
            } else {
                entries = TicketDao.getAllTickets(userEntry);
            }
            ctx.json(entries);// jackson variation objectMapper.writeValueAsString(request)
        } catch (UserSessionExpiredException ex) {
            ctx.status(401);
            ctx.result("You are not authorized!");
        } catch (Exception ex) {
            ctx.status(400);
            ctx.result("Bad format");
        }
    }

    protected  class Approved {
        public Approved() {
            this.approve = false;
        }

        public boolean isApprove() {
            return approve;
        }

        public void setApprove(boolean approve) {
            this.approve = approve;
        }

        private boolean approve;
    }

    public static void approveTicket(Context ctx) {
        try {
            Approved entry = new ObjectMapper().readValue(ctx.body(), Approved.class);
            // Manager check
            {
                UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
                if (!userEntry.isManager()) {
                    throw new UnauthorizedException();
                }
            }

            long tickedId = TicketDao.createTicket(entry.isApprove());

            ctx.status(200);
            ctx.result("Ticked status updated:" + tickedId);
        }catch (UserSessionExpiredException e) {
            ctx.status(401);
            ctx.result("You are not authenticated!");
        } catch (UnauthorizedException e) {
            ctx.status(403);
            ctx.result("You are not authorized to approve!");
        } catch (TicketNotAddedException e) {
            ctx.status(500);
            ctx.result("Server error:" + e.getMessage());
        } catch (SQLException ex) {
            ctx.status(500);
            ctx.result("Server error");
        } catch (JsonProcessingException e) {
            ctx.status(500);
            ctx.result("Bad input");
        }
    }

    public static void submitTicket(Context ctx) {
        try {
            // Must have an amount, description and cookie.
            TicketEntry ticketEntry = TicketController.validateSubmit(ctx);
            // Must be authorised.
            UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));

            ticketEntry.setUsername(userEntry.getUsername());
            long id = TicketDao.submitTicket(ticketEntry);
            ctx.status(200);
            ctx.result("Ticked status updated:" + id);
        } catch (JsonProcessingException ex) {
            ctx.status(400);
            ctx.result(ex.getMessage());
        } catch (UserSessionExpiredException e) {
            ctx.status(401);
            ctx.result("You are not authorized!");
        } catch (SQLException ex) {
            ctx.status(500);
            ctx.result("Server error");
        }
    }

    private static TicketEntry validateSubmit(Context ctx) throws JsonProcessingException {
        TicketEntry entry = new ObjectMapper().readValue(ctx.body(), TicketEntry.class);
        if (-1 == entry.getAmount() || null == entry.getDescription() || null == ctx.cookie("session")) {
            throw new BadTicketException("Bad user input");
        }
        return entry;
    }
}
