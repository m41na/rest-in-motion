package works.hop.jetty;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerResult;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class AsyncSuccessTest {

    private AsyncSuccess success;

    private String target = "/";
    @Mock
    private AsyncContext async;
    @Mock
    private JettyRequest request;
    @Mock
    private JettyResponse response;
    @Mock
    private ServletOutputStream out;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.success = new AsyncSuccess(async, target, request, response);
        doNothing().when(out).write(anyInt());
        when(response.getContent()).thenReturn("Some great content".getBytes());
        when(response.getOutputStream()).thenReturn(out);
    }

    @Test
    public void apply() {
        HandlerResult reply = new HandlerResult();
        HandlerResult res = success.apply(reply);
        assertNotNull(res);
        verify(async, times(1)).complete();
    }
}