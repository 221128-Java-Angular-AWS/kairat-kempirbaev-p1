package ticketController;

import Exceptions.BadTicketException;
import Exceptions.TicketNotAddedException;
import Exceptions.UnauthorizedException;
import Exceptions.UserSessionExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.json.JSONObject;
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

    public static void approveTicket(Context ctx) {
        try {
            JSONObject json = new JSONObject(ctx.body());
            // Manager check
            {
                UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
                if (!userEntry.isManager()) {
                    throw new UnauthorizedException();
                }
            }

            long tickedId = TicketDao.createTicket(json.getBoolean("approved"));

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
