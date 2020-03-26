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
    HandlerFunction successHandler;
    HandlerFunction failureHandler;
    HandlerFunction successInterceptor;
    HandlerFunction failureInterceptor;
    OnSuccessPromise onSuccessPromise;
    OnFailurePromise onFailurePromise;
    @Mock
    AuthInfo authInfo;
    @Mock
    ARequest request;
    @Mock
    AResponse response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chain = new DefaultHandlerChain();
        onSuccessPromise = new OnSuccessPromise();
        onFailurePromise = new OnFailurePromise();
        promise = new BasicHandlerPromise(onSuccessPromise, onFailurePromise);
        successHandler = (auth, req, res, done) -> {
            String output = "Handler Function Success - simulate successful handler";
            promise.resolve(() -> System.out.println(output));
        };
        failureHandler = (auth, req, res, done) -> {
            String output = "Handler Function Failure - simulate failure handler";
            throw new HandlerException(500, output);
        };
        successInterceptor = (auth, req, res, done) -> {
            System.out.println("Handler Intercept Success - simulate successful interceptor");
            promise.next();
        };
        failureInterceptor = (auth, req, res, done) -> {
            String output = "Handler Intercept Failure - simulate failure interceptor";
            promise.failed(output + " - failed to continue chain");
        };
    }

    @Test(expected = HandlerException.class)
    public void interceptorsCreatingALoop() {
        chain.addLast(successInterceptor);
        chain.addLast(failureInterceptor);
        chain.addLast(successInterceptor);
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
        chain.addLast(successInterceptor);
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNotNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNull(result2);
    }

    @Test
    public void failingInterceptorsBeforeSuccessHandler() {
        chain.addLast(failureInterceptor);
        chain.addLast(successHandler);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNotNull(result2);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
        assertEquals("Handler Intercept Failure - simulate failure interceptor - failed to continue chain", failure.getMessage());
    }

    @Test
    public void failingInterceptorsWithFailureHandler() {
        chain.addLast(failureInterceptor);
        chain.addLast(failureHandler);
        chain.intercept(failureHandler, authInfo, request, response, promise);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
        assertEquals("Handler Intercept Failure - simulate failure interceptor - failed to continue chain", failure.getMessage());
    }

    @Test
    public void successInterceptorsWithSuccessHandler() {
        chain.addLast(successInterceptor);
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
        chain.addLast(successInterceptor);
        chain.addLast(failureHandler);
        chain.intercept(failureHandler, authInfo, request, response, promise);
    }

    @Test
    public void successInterceptorsWithFailureInterceptorAfterSuccessHandler() {
        chain.addLast(successInterceptor);
        chain.addLast(successHandler);
        chain.addLast(failureInterceptor);
        chain.intercept(successHandler, authInfo, request, response, promise);
        HandlerResult result = onSuccessPromise.result;
        assertNotNull(result);
        HandlerResult result2 = onFailurePromise.result;
        assertNotNull(result2);
        Throwable failure = onFailurePromise.failure;
        assertNotNull(failure);
    }
}