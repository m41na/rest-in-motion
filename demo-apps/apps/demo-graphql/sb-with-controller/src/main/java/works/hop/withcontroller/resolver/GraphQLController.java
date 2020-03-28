package works.hop.withcontroller.resolver;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/graphql")
public class GraphQLController {

    @Autowired
    private GraphQL graphql;

    @GetMapping
    public @ResponseBody
    ExecutionResult handleGet(@RequestParam("query") String query) {
        return graphql.execute(query);
    }

    @PostMapping
    public @ResponseBody
    ExecutionResult handlePost(@RequestBody String query) {
        return graphql.execute(query);
    }
}
