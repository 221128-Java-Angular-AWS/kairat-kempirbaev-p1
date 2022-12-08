package ticketController;

import Exceptions.BadInputException;
import Exceptions.TicketNotAddedException;
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
            List<TicketEntry> entries =  TicketDao.getAllTickets(userEntry);
            ctx.json(entries);// jackson variation objectMapper.writeValueAsString(request)
        }catch(UserSessionExpiredException ex){
            ctx.status(401);
            ctx.result("You are not authorized!");
        }catch (Exception ex) {
            ctx.status(400);
            ctx.result("Bad format");
        }
    }

    public static void addTicket(Context ctx) {
        try {
            TicketEntry ticketEntry = TicketController.validateInput(ctx);
            UserEntry userEntry = UserDao.getSessionUser(ctx.cookie("session"));
            ticketEntry.setUsername(userEntry.getUsername());
            TicketDao.addTicket(ticketEntry);
        }catch(BadInputException ex){
            ctx.status(400);
            ctx.result("Bad format");
        }catch(JsonProcessingException ex){
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


    private static TicketEntry validateInput(Context ctx) throws JsonProcessingException, BadInputException {
        TicketEntry entry = new ObjectMapper().readValue(ctx.body(), TicketEntry.class);
        if (-1 == entry.getAmount() || null == entry.getDescription() || null == ctx.cookie("session")) {
            throw new BadInputException();
        }
        return entry;
    }

}
