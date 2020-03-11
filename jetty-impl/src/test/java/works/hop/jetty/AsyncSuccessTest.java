package works.hop.jetty;

import com.ctc.wstx.shaded.msv_core.verifier.IVerifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.handler.HandlerResult;

import javax.servlet.AsyncContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AsyncSuccessTest {

    private AsyncSuccess success;

    private String target = "/";
    @Mock
    private AsyncContext async;
    @Mock
    private JettyRequest request;
    @Mock
    private JettyResponse response;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.success = new AsyncSuccess(async, target, request, response);
    }

    @Test
    public void apply() {
        HandlerResult reply = new HandlerResult();
        HandlerResult res = success.apply(reply);
        assertNotNull(res);
        verify(async, times(1)).complete();
    }
}