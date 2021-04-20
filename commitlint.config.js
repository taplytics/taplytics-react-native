module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'scope-enum': [2, 'always', ['ios', 'android', 'hooks', 'push', 'experiments', 'user', 'release']],
  },
}
