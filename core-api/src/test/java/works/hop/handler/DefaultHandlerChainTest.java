package works.hop.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;
import works.hop.handler.impl.DefaultHandlerChain;

import static org.junit.Assert.*;

public class DefaultHandlerChainTest {

    HandlerChain chain;
    HandlerPromise promise;
    HandlerIntercept successHandler;
    HandlerIntercept failureHandler;
    HandlerIntercept successInterceptor1;
    HandlerIntercept successInterceptor2;
    HandlerIntercept failingInterceptor1;
    HandlerIntercept failureInterceptor2;
    OnSuccessPromise onSuccessPromise;
    OnFailurePromise onFailurePromise;
    @Mock
    AuthInfo authInfo;
    @Mock
    ARequest request;
    @Mock
    AResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        chain = new DefaultHandlerChain();
        onSuccessPromise = new OnSuccessPromise();
        onFailurePromise = new OnFailurePromise();
        promise = new BasicHandlerPromise(onSuccessPromise, onFailurePromise);
        successHandler = new SuccessHandlerFunction();
        failureHandler = new FailureHandlerFunction();
        successInterceptor1 = new HandlerInterceptSuccess1();
        successInterceptor2 = new HandlerInterceptSuccess2();
        failingInterceptor1 = new HandlerInterceptFailure1();
        failureInterceptor2 = new HandlerInterceptFailure2();
    }

    @Test(expected = HandlerException.class)
    public void interceptorsCreatingALoop() {
        chain.addLast(successInterceptor1);
        chain.addLast(failingInterceptor1);
        chain.addLast(successInterceptor1);
    }

    @Test
    public void noInterceptorsAddedToSuccessHandler() {
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        assertNotNull(onSuccessPromise.result);
    }

    @Test(expected = HandlerException.class)
    public void noInterceptorsAddedToFailureHandler() {
        chain.addLast(failureHandler);
        chain.intercept(failureHandler, authInfo, request, response, promise);
    }

    @Test
    public void successInterceptorsBeforeSuccessHandler() {
        chain.addLast(successInterceptor1);
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNotNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNull(result2);
    }

    @Test(expected = HandlerException.class)
    public void failingInterceptorsBeforeSuccessHandler() {
        chain.addLast(failingInterceptor1);
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNotNull(result2);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
        assertEquals("HandlerInterceptFailure1 - failed to continue chain", failure.getMessage());
    }

    @Test(expected = HandlerException.class)
    public void failingInterceptorsWithFailureHandler() {
        chain.addLast(failingInterceptor1);
        chain.addLast(failureHandler);
        chain.intercept(failureHandler, authInfo, request, response, promise);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
        assertEquals("HandlerInterceptFailure1 - failed to continue chain", failure.getMessage());
    }

    @Test
    public void successInterceptorsWithSuccessHandler() {
        chain.addLast(successInterceptor1);
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNotNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNull(result2);
        Throwable failure = onFailurePromise.failure;
        assertNull(failure);
    }

    @Test(expected = HandlerException.class)
    public void successInterceptorsWithFailureHandler() {
        chain.addLast(successInterceptor1);
        chain.addLast(failureHandler);
        chain.intercept(failureHandler, authInfo, request, response, promise);
    }

    @Test(expected = HandlerException.class)
    public void successInterceptorsWithFailureInterceptorAfterSuccessHandler() {
        chain.addLast(successInterceptor1);
        chain.addLast(successHandler);
        chain.addLast(failingInterceptor1);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNotNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNotNull(result2);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
    }
}