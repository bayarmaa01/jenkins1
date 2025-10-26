pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                echo "Building ${env.BRANCH_NAME} branch..."
            }
        }

        stage('Test') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        echo "[main] Run full integration tests"
                    } else if (env.BRANCH_NAME == 'develop') {
                        echo "[develop] Run regression tests"
                    } else if (env.BRANCH_NAME.startsWith('feature/')) {
                        echo "[feature/*] Run quick unit tests"
                    } else {
                        echo "Other branch – basic checks"
                    }
                }
            }
        }

        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo "Deploying ${env.BRANCH_NAME} branch..."
            }
        }
    }

    post {
        success {
            echo "✅ ${env.BRANCH_NAME} build completed successfully."
        }
        failure {
            echo "❌ ${env.BRANCH_NAME} build failed."
        }
    }
}
