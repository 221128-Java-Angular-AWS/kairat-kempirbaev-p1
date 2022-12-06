import io.javalin.*;
import io.javalin.http.Context;
import org.junit.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class MainTest {

    private final Context ctx = mock(Context.class);

    @Test
    public void POST_to_create_user() {
        when(ctx.queryParam("username")).thenReturn("Roland");
        when(ctx.queryParam("password")).thenReturn("password");
        verify(ctx).status(201);
        assertTrue(false);
    }

    @Test
    public void POST_to_create_user_malformed() {
        when(ctx.queryParam("username")).thenReturn(null);
        when(ctx.queryParam("password")).thenReturn("password");
        verify(ctx).status(400);
        assertTrue(false);
    }

}
