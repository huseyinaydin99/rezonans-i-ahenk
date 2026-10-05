with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'r') as f:
    content = f.read()

# Change return type of transactionBehavior to IPipelineBehavior
content = content.replace('public TransactionBehavior<?, ?> transactionBehavior(', 'public tr.com.huseyinaydin.application.cqrs.IPipelineBehavior<?, ?> transactionBehavior(')

with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'w') as f:
    f.write(content)
