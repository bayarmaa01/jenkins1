node {
  try {
    stage('Checkout') {
      echo "Cloning repository..."
      git branch: 'main', url: 'https://github.com/BayarmaaBumandorj/my-maven-app.git'
      sh 'ls -la'
    }

    stage('Parallel Tests') {
      parallel(
        unit: {
          stage('Unit Tests') {
            echo "Running unit tests..."
            dir('.') {
              sh 'mvn -Dtest=**/*UnitTest test || true'
              junit '**/target/surefire-reports/*.xml'
            }
          }
        },
        integration: {
          stage('Integration Tests') {
            echo "Running integration tests..."
            dir('.') {
              sh 'mvn -Pintegration-test verify || true'
              junit '**/target/failsafe-reports/*.xml'
            }
          }
        }
      )
    }
  } catch (e) {
    currentBuild.result = 'FAILURE'
    echo "Build failed due to: ${e}"
    throw e
  } finally {
    stage('Archive Reports') {
      echo "Archiving reports..."
      archiveArtifacts artifacts: '**/target/*.xml, **/target/*.log', allowEmptyArchive: true
    }

    stage('Notify') {
      script {
        if (currentBuild.result == 'FAILURE') {
          echo "Sending notification: build failed"
        } else {
          echo "Build succeeded ✅"
        }
      }
    }
  }
}
