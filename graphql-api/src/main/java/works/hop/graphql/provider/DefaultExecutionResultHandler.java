package works.hop.graphql.provider;

import graphql.ExecutionResult;
import works.hop.graphql.api.ExecutionResultHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultExecutionResultHandler implements ExecutionResultHandler {

    @Override
    public CompletableFuture<Map<String, Object>> handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF) {
        return executionResultCF.thenApply(ExecutionResult::toSpecification);
    }
}

