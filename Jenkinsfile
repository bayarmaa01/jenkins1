node {
  // status flags
  def unitStatus = 'NOT_RUN'
  def integrationStatus = 'NOT_RUN'
  def projectDir = ''

  try {
    stage('Checkout') {
      echo "Checking out source..."
      checkout scm
      // find pom.xml: prefer workspace root, else search up to 3 levels
      projectDir = sh(
        script: """\
          if [ -f pom.xml ]; then
            echo "."
          else
            find . -maxdepth 3 -type f -name pom.xml -printf '%h\\n' | head -n1 || true
          fi
        """.stripIndent(),
        returnStdout: true
      ).trim()
      if (!projectDir) {
        error "No pom.xml found in workspace (searched up to depth 3). Please ensure the Maven project (pom.xml) is in the repo."
      }
      echo "Maven project directory: ${projectDir}"
    }

    stage('Build') {
      echo "Building the project in ${projectDir} (skip tests during package)..."
      dir(projectDir) {
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Parallel Tests') {
      parallel(
        unit: {
          stage('Unit Tests') {
            try {
              dir(projectDir) {
                echo "Running unit tests (mvn test -DskipITs)..."
                // run unit tests (Surefire)
                sh 'mvn -B -DskipITs=true test'
                // publish JUnit results produced by surefire
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
              }
              unitStatus = 'SUCCESS'
            } catch (err) {
              unitStatus = 'FAILURE'
              echo "Unit tests failed: ${err}"
              throw err
            }
          }
        },
        integration: {
          stage('Integration Tests') {
            try {
              dir(projectDir) {
                echo "Running integration tests (failsafe verify)..."
                // run integration tests (Failsafe) - typical profile/goal; adjust to your project
                sh 'mvn -B -DskipTests=false -Pintegration-test verify'
                // publish JUnit results produced by failsafe
                junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
              }
              integrationStatus = 'SUCCESS'
            } catch (err) {
              integrationStatus = 'FAILURE'
              echo "Integration tests failed: ${err}"
              throw err
            }
          }
        }
      ) // end parallel
    }

  } catch (e) {
    // mark build as failed and rethrow so finally runs
    currentBuild.result = 'FAILURE'
    echo "Pipeline failed: ${e}"
    throw e
  } finally {
    stage('Archive reports & artifacts') {
      // archive artifacts relative to workspace root
      archiveArtifacts artifacts: "${projectDir}/target/*.jar, ${projectDir}/target/*.zip, ${projectDir}/target/*.xml, ${projectDir}/target/*.log", allowEmptyArchive: true

      // Publish any remaining junit results (catch errors)
      try {
        junit allowEmptyResults: true, testResults: "${projectDir}/target/surefire-reports/*.xml, ${projectDir}/target/failsafe-reports/*.xml"
      } catch (x) {
        echo "No junit files found or publish failed: ${x}"
      }
    }

    stage('Notify on failure') {
      if (currentBuild.currentResult == 'FAILURE' || currentBuild.currentResult == 'UNSTABLE') {
        echo "Build ${currentBuild.currentResult} - Unit: ${unitStatus}, Integration: ${integrationStatus}"
        // Try to send email if email-ext is installed; otherwise just echo message
        try {
          emailext (
            subject: "[Jenkins] ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
            body: """Job: ${env.JOB_NAME}
Build: ${env.BUILD_URL}
Project dir: ${projectDir}
Unit status: ${unitStatus}
Integration status: ${integrationStatus}
Console: ${env.BUILD_URL}console""",
            to: 'dev-team@example.com'
          )
        } catch (mailErr) {
          echo "emailext not available or failed to send: ${mailErr}"
          echo "Would notify: dev-team@example.com"
        }
      } else {
        echo "Build succeeded - no failure notification needed."
      }
    }
  } // finally
} // node
