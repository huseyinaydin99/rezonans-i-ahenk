import os
behaviors = ['ValidationBehavior.java', 'MembershipTierBehavior.java']
for b in behaviors:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/{b}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = content.replace('class ValidationBehavior implements', 'class ValidationBehavior<TRequest, TResponse> implements')
        content = content.replace('class MembershipTierBehavior implements', 'class MembershipTierBehavior<TRequest, TResponse> implements')
        with open(path, 'w') as f:
            f.write(content)
