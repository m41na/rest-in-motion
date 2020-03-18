package works.hop.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import works.hop.core.ARequest;
import works.hop.core.AResponse;
import works.hop.core.AuthInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HandlerChainTest {

    HandlerChain chain;
    HandlerFunction successHandler;
    HandlerFunction failureHandler;
    HandlerPromise promise;
    HandlerIntercept beforeIsOkIntercept;
    HandlerIntercept beforeNotOkIntercept;
    HandlerIntercept otherwiseNotOkIntercept;
    HandlerIntercept afterNotOkIntercept;
    @Mock
    AuthInfo authInfo;
    @Mock
    ARequest request;
    @Mock
    AResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        chain = new BasicHandlerChain();
        successHandler = new SuccessHandlerFunction();
        failureHandler = new FailureHandlerFunction();
        promise = new BasicHandlerPromise();
        beforeIsOkIntercept = new BeforeIsOkIntercept();
        beforeNotOkIntercept = new BeforeNotOkIntercept();
        otherwiseNotOkIntercept = new OtherwiseNotOkIntercept();
        afterNotOkIntercept = new AfterNotOkIntercept();
    }

    @Test(expected = HandlerException.class)
    public void interceptorsCreatingALoop() {
        chain.addLast(beforeIsOkIntercept);
        chain.addLast(beforeNotOkIntercept);
        chain.addLast(beforeIsOkIntercept);
    }

    @Test(expected = HandlerException.class)
    public void noInterceptorsAdded() {
        CompletableFuture future = chain.intercept(successHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
    }

    @Test
    public void interceptorsWithSuccess() {
        chain.addLast(beforeIsOkIntercept);
        CompletableFuture future = chain.intercept(successHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
    }

    @Test(expected = CompletionException.class)
    public void interceptorsWithFailureHandler() {
        chain.addLast(beforeIsOkIntercept);
        CompletableFuture<HandlerResult> future = chain.intercept(failureHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
        HandlerResult result = future.join();
        assertFalse(result.success());
    }

    @Test(expected = CompletionException.class)
    public void interceptWithFailure_Before() {
        chain.addLast(beforeNotOkIntercept);
        chain.addLast(beforeIsOkIntercept);
        CompletableFuture<HandlerResult> future = chain.intercept(successHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
        HandlerResult result = future.join();
        assertFalse(result.success());
    }

    @Test(expected = CompletionException.class)
    public void interceptWithFailure_Otherwise() {
        chain.addLast(otherwiseNotOkIntercept);
        chain.addLast(beforeIsOkIntercept);
        CompletableFuture<HandlerResult> future = chain.intercept(successHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
        HandlerResult result = future.join();
        assertFalse(result.success());
    }

    @Test(expected = CompletionException.class)
    public void interceptWithFailure_After() {
        chain.addLast(afterNotOkIntercept);
        chain.addLast(beforeIsOkIntercept);
        CompletableFuture<HandlerResult> future = chain.intercept(successHandler, authInfo, request, response, promise);
        assertTrue(future.isDone());
        HandlerResult result = future.join();
        assertFalse(result.success());
    }
}