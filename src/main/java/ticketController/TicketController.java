package ticketController;

import Exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.json.JSONObject;
import userController.UserDao;
import userController.UserEntry;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TicketController {
    private TicketDao ticketDao;
    private UserDao userDao;

    public TicketController(TicketDao ticketDao, UserDao userDao) {
        this.ticketDao = ticketDao;
        this.userDao = userDao;
    }

    public  void getTickets(Context ctx) {
        try {
            final String PENDING = "pending",
                    APPROVED = "approved";
            List<TicketEntry> entries = new LinkedList<>();
            //Provided  URI?approved=BOOLEAN&pending=BOOLEAN
            Map<String, List<String>> params = ctx.queryParamMap();
            boolean isPending  = ctx.queryParamAsClass(PENDING, Boolean.class).getOrDefault(false),
                    isApproved = ctx.queryParamAsClass(APPROVED, Boolean.class).getOrDefault(false);

            // URI : /tickets/{uname}
            // Authenticated and Authorized check
            UserEntry userEntry = userDao.getSessionUser(ctx.cookie("session"));
            if(!ctx.pathParam("uname").equals(userEntry.getUsername())){
                throw new UserSessionExpiredException();
            };

            //Manager
            if (userEntry.isManager()){
                ctx.json(ticketDao.getPendingTickets());
                ctx.status(200);
                return;
            }

            //Employee
            // No Query Parameter
            if(! (params.containsKey(APPROVED) || params.containsKey(PENDING)) ) {
                entries = ticketDao.getAllTickets(userEntry);
                ctx.json(entries);
                ctx.status(200);
                return;
            }

            //Query Parameter SET
            //Fetch approved query parameter
            if(params.containsKey(APPROVED)){
                entries.addAll(ticketDao.getApprovedTickets(userEntry, isApproved));
            }

            //Fetch pending query parameter
            if(params.containsKey(PENDING)){
                if(isPending){
                    entries.addAll(ticketDao.getPendingTickets(userEntry));
                }else{
                    if(!params.containsKey(APPROVED)){
                        entries.addAll(ticketDao.getProcessedTickets(userEntry));
                    }
                }
            }

            ctx.json(entries);
            ctx.status(200);
        } catch (UserSessionExpiredException ex) {
            ctx.status(401);
            ctx.result("You are not authorized!");
        } catch (Exception ex) {
            ctx.status(400);
            ctx.result("Bad format");
        }
    }

    public  void approveTicket(Context ctx) {
        try {
            // URI /tickets/
            JSONObject json = new JSONObject(ctx.body());
            // Manager check
            {
                UserEntry userEntry = userDao.getSessionUser(ctx.cookie("session"));
                if (!userEntry.isManager()) {
                    throw new UnauthorizedException();
                }
            }
            long tickedId = ticketDao.createTicket(json.getBoolean("approved"));

            ctx.status(200);
            ctx.result("Ticked status updated:" + tickedId);
        }catch (UserSessionExpiredException e) {
            ctx.status(401);
            ctx.result("You are not authenticated!");
        } catch (UnauthorizedException e) {
            ctx.status(403);
            ctx.result("You are not authorized to approve!");
        } catch (TicketNotAddedException e) {
            ctx.status(409);
            ctx.result("Error:" + e.getMessage());
        } catch (SQLException ex) {
            ctx.status(500);
            ctx.result("Server error");
        }
    }

    public  void submitTicket(Context ctx) {
        try {
            // Must have an amount, description and cookie.
            TicketEntry ticketEntry = validateSubmit(ctx);
            // Must be authorised.
            UserEntry userEntry = userDao.getSessionUser(ctx.cookie("session"));
            if(!ctx.pathParam("uname").equals(userEntry.getUsername())){
                throw new UserSessionExpiredException();
            };

            ticketEntry.setUsername(userEntry.getUsername());
            long id = ticketDao.submitTicket(ticketEntry);
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

    private TicketEntry validateSubmit(Context ctx) throws JsonProcessingException {
        TicketEntry entry = new ObjectMapper().readValue(ctx.body(), TicketEntry.class);
        if (-1 == entry.getAmount() || null == entry.getDescription() || null == ctx.cookie("session")) {
            throw new BadTicketException("Bad user input");
        }
        return entry;
    }
}
