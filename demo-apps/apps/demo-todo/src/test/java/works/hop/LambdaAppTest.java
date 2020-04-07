package works.hop;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class LambdaAppTest {

    @Test
    public void testHandleRequest() {
        LambdaApp app = new LambdaApp();
        Map<String, String> result = app.handleRequest(Map.of("name", "json"), new MockContext());
        System.out.println(result);
        assertTrue(result.size() > 1);
    }

    class MockLogger implements LambdaLogger {

        @Override
        public void log(String s) {
            System.out.println(s);
        }

        @Override
        public void log(byte[] bytes) {
            System.out.println(new String(bytes));
        }
    }

    class MockContext implements Context {

        LambdaLogger logger = new MockLogger();

        @Override
        public String getAwsRequestId() {
            return null;
        }

        @Override
        public String getLogGroupName() {
            return null;
        }

        @Override
        public String getLogStreamName() {
            return null;
        }

        @Override
        public String getFunctionName() {
            return null;
        }

        @Override
        public String getFunctionVersion() {
            return null;
        }

        @Override
        public String getInvokedFunctionArn() {
            return null;
        }

        @Override
        public CognitoIdentity getIdentity() {
            return null;
        }

        @Override
        public ClientContext getClientContext() {
            return null;
        }

        @Override
        public int getRemainingTimeInMillis() {
            return 0;
        }

        @Override
        public int getMemoryLimitInMB() {
            return 0;
        }

        @Override
        public LambdaLogger getLogger() {
            return logger;
        }
    }
}
