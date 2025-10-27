pipeline {
  agent any
  environment {
    // Replace with your actual Docker Hub repo (already used in your pipeline logs)
    DOCKERHUB_REPO = "bayarmaa/jenkins-demo"
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    IMAGE = "${DOCKERHUB_REPO}:${IMAGE_TAG}"
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Checkout') {
      steps {
        echo "Checkout from SCM..."
        checkout scm
      }
    }

    stage('Show files') {
      steps {
        echo "Workspace contents:"
        sh 'pwd; ls -la'
      }
    }

    stage('Build Docker image') {
      steps {
        echo "Building Docker image ${env.IMAGE}"
        // Use BuildKit if available (optional). Fallback to normal build.
        sh '''
          # enable BuildKit if available (non-fatal)
          export DOCKER_BUILDKIT=1 || true
          docker --version
          docker build -t ${IMAGE} .
        '''
      }
    }

    stage('Login & Push to Docker Hub') {
      steps {
        script {
          // Use the Jenkins credential ID you have (from your log: dockerhub-credentials)
          withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
            sh '''
              set -e
              echo "Logging in as ${DOCKERHUB_USER} (token masked)"
              echo "${DOCKERHUB_PASS}" | docker login -u "${DOCKERHUB_USER}" --password-stdin
              docker push ${IMAGE}
              docker logout
            '''
          }
        }
      }
    }
  } // stages

  post {
    success {
      echo "Docker image pushed: ${IMAGE}"
    }
    failure {
      echo "Build failed — image not pushed"
    }
    always {
      // cleanup local image to free disk space
      sh '''
        docker rmi ${IMAGE} || true
      '''
    }
  }
}
