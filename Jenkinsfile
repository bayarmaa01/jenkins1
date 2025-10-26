@Library('my-shared-lib') _
pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        echo "Building..."
        sh 'echo build step placeholder'
      }
    }

    stage('Info') {
      steps {
        // call shared library function - optional prefix
        printBuildInfo(prefix: '>>')
      }
    }

    stage('Test') {
      steps {
        echo "Running tests..."
        sh 'echo tests placeholder'
      }
    }
  }

  post {
    always {
      echo 'Pipeline finished'
    }
  }
}