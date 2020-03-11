package works.hop.jetty;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerException;
import works.hop.handler.HandlerResult;

import javax.servlet.AsyncContext;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AsyncFailureTest {

    private AsyncFailure failure;

    private String target = "/";
    @Mock
    private AsyncContext async;
    @Mock
    private JettyResponse response;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.failure = new AsyncFailure(async, target, response);
    }

    @Test
    public void apply() throws IOException {
        HandlerResult reply = new HandlerResult();
        HandlerResult res = failure.apply(reply, new HandlerException(404, "Test failure"));
        assertNotNull(res);
        verify(async, times(1)).complete();
        verify(response, times(1)).sendError(anyInt(), anyString());
    }
}