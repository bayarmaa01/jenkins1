node {
  try {
    stage('Checkout') {
      checkout scm
    }

    stage('Parallel Tests') {
      parallel(
        unit: {
          stage('Unit Tests') {
            sh 'echo Running unit tests...'
            sh 'mvn -Dtest=**/*UnitTest test' // example
            junit '**/target/surefire-reports/*.xml'
          }
        },
        integration: {
          stage('Integration Tests') {
            sh 'echo Running integration tests...'
            sh 'mvn -Pintegration-test verify' // example
            junit '**/target/failsafe-reports/*.xml'
          }
        }
      )
    }
  } catch (e) {
    currentBuild.result = 'FAILURE'
    throw e
  } finally {
    stage('Archive reports') {
      archiveArtifacts artifacts: '**/target/*.xml, **/target/*.log', allowEmptyArchive: true
    }
    stage('Notify') {
      script {
        if (currentBuild.result == 'FAILURE') {
          echo "Sending notification: build failed"
          // integrate with email/slack plugin or curl to webhook
        }
      }
    }
  }
}
