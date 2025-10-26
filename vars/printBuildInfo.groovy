def call(Map opts = [:]) {
  def prefix = opts.get('prefix', '')
  if (prefix) { prefix = "${prefix} " }

  def buildNum = (env.BUILD_NUMBER ?: 'N/A')
  def branch = (env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'N/A')

  echo "${prefix}Build number: ${buildNum}"
  echo "${prefix}Branch: ${branch}"
}