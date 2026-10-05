import os

for b in ['CachingBehavior.java', 'LoggingBehavior.java']:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/{b}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = content.replace('implements IPipelineBehavior {', 'implements IPipelineBehavior<TRequest, TResponse> {')
        with open(path, 'w') as f:
            f.write(content)

tier_factory = 'music-application/src/main/java/tr/com/huseyinaydin/application/membership/rules/MembershipTierFactory.java'
if os.path.exists(tier_factory):
    with open(tier_factory, 'r') as f:
        content = f.read()
    # Replace "case MembershipTier." with "case " again in case it failed
    content = content.replace('case MembershipTier.', 'case ')
    with open(tier_factory, 'w') as f:
        f.write(content)

