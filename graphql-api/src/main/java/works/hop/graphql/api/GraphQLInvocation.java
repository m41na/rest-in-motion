package works.hop.graphql.api;

import graphql.ExecutionResult;

import java.util.concurrent.CompletableFuture;

public interface GraphQLInvocation {

    CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData);
}
