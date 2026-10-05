with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'r') as f:
    content = f.read()

no_op_txn = """
    static class NoOpTransactionBehavior<TRequest, TResponse> implements tr.com.huseyinaydin.application.cqrs.IPipelineBehavior<TRequest, TResponse> {
        @Override
        public TResponse handle(TRequest request, tr.com.huseyinaydin.application.cqrs.PipelineDelegate<TResponse> next) {
            return next.proceed();
        }
    }
}
"""

content = content.replace('}\n}\n', '}\n' + no_op_txn)
with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'w') as f:
    f.write(content)
