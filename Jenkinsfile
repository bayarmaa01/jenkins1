node {
    stage('Checkout') {
        echo 'Checking out source code...'
        checkout scm
    }

    stage('Build') {
        echo 'Building the application...'
        sh 'mvn clean package -DskipTests'
    }

    stage('Parallel Tests') {
        parallel(
            "Unit Tests": {
                stage('Unit Tests') {
                    try {
                        echo 'Running unit tests...'
                        sh 'mvn test -Dtest=*UnitTest'
                        junit '**/target/surefire-reports/*.xml'
                    } catch (err) {
                        echo "❌ Unit tests failed: ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            },
            "Integration Tests": {
                stage('Integration Tests') {
                    try {
                        echo 'Running integration tests...'
                        sh 'mvn verify -Dtest=*IntegrationTest'
                        junit '**/target/failsafe-reports/*.xml'
                    } catch (err) {
                        echo "❌ Integration tests failed: ${err}"
                        currentBuild.result = 'FAILURE'
                        throw err
                    }
                }
            }
        )
    }

    stage('Post-build Actions') {
        echo 'Archiving reports...'
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'
    }
}

post {
    failure {
        echo 'Build failed! Sending email notification...'
        emailext(
            to: 'b.bayarmaa0321@gmail.com',
            subject: "❌ Jenkins Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            body: """
                <p>Build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> failed.</p>
                <p>Check console output at: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
            """,
            attachLog: true
        )
    }
    success {
        echo '✅ Build succeeded!'
    }
}
