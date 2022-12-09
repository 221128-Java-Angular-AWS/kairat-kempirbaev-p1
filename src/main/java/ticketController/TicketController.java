package ticketController;

import Exceptions.BadInputException;
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

    public static void addTicket(Context ctx) {
        try {
            TicketEntry ticketEntry = TicketController.validateCreate(ctx);
            UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
            ticketEntry.setUsername(userEntry.getUsername());
            TicketDao.addTicket(ticketEntry);
        } catch (BadInputException ex) {
            ctx.status(400);
            ctx.result("Bad format");
        } catch (JsonProcessingException ex) {
            ctx.status(400);
            ctx.result("Bad format");
        } catch (SQLException ex) {
            ctx.status(500);
            ctx.result("Server error");
        } catch (TicketNotAddedException ex) {
            ctx.status(500);
            ctx.result("Didn't add a ticket");
        } catch (UserSessionExpiredException e) {
            ctx.status(401);
            ctx.result("You are not authorized!");
        }
    }

    public static void approveTicket(Context ctx) {
        try {
            TicketEntry entry = new ObjectMapper().readValue(ctx.body(), TicketEntry.class);
            if (-1L ==  entry.getId() ){
                throw new BadInputException();
            }
            // Authorize
            {
                UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
                if (!userEntry.isManager()) {
                    throw new UnauthorizedException();
                }
            }
            TicketDao.updateTicket(entry);
            ctx.status(200);
            ctx.result("Ticked status updated!");
        } catch (BadInputException ex) {
            ctx.status(400);
            ctx.result("Bad format");
        } catch (JsonProcessingException ex) {
            ctx.status(400);
            ctx.result("Bad format");
        } catch (SQLException ex) {
            ctx.status(500);
            ctx.result("Server error");
        } catch (UserSessionExpiredException e) {
            ctx.status(401);
            ctx.result("You are not authenticated!");
        } catch (UnauthorizedException e) {
            ctx.status(403);
            ctx.result("You are not authorized to approve!");
        }
    }

    private static TicketEntry validateCreate(Context ctx) throws JsonProcessingException, BadInputException {
        TicketEntry entry = new ObjectMapper().readValue(ctx.body(), TicketEntry.class);
        if (-1 == entry.getAmount() || null == entry.getDescription() || null == ctx.cookie("session")) {
            throw new BadInputException();
        }
        return entry;
    }
}
