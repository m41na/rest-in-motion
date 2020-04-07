package works.hop;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaApp implements RequestHandler<Map<String, String>, Map<String, String>> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Map<String, String> handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String response = new String("200 OK");
        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        // process event
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());
        //prepare response
        Map<String, String> env = new HashMap<>();
        env.putAll(event);
        env.putAll(System.getenv());
        env.put("ctx", gson.toJson(context));
        return env;
    }
}
