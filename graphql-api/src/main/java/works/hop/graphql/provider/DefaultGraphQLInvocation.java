package works.hop.graphql.provider;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import works.hop.graphql.api.GraphQLInvocation;
import works.hop.graphql.api.GraphQLInvocationData;

import java.util.concurrent.CompletableFuture;

public class DefaultGraphQLInvocation implements GraphQLInvocation {

    private GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .build();
        return graphQL.executeAsync(executionInput);
    }
}

