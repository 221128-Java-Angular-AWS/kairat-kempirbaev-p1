import io.javalin.Javalin;
public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
        app.get("/login", ctx -> ctx.result("Trying to log in"));
        app.get("/user/*", ctx -> {
            ctx.result("You are here because " + ctx.path().split("/")[2] + " matches " );
        });
        app.get("/admin/approve", ctx -> ctx.result("Trying to approve reimbursement"));
        app.get("/admin/view", ctx -> ctx.result("Get all reimbursements"));
    }
}


