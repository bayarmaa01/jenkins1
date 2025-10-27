pipeline {
  agent { label '' } // empty means any available agent; ensure agent has docker CLI
  environment {
    DOCKERHUB_REPO = "bayarmaa/jenkins-demo"   // CHANGE this to your Docker Hub repo
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    IMAGE = "${env.DOCKERHUB_REPO}:${env.IMAGE_TAG}"
  }

  options {
    // keep timestamps to help debug
    timestamps()
    // keep last 10 builds
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build Docker image') {
      steps {
        echo "Building Docker image ${env.IMAGE}"
        // use docker CLI to build (requires docker on agent)
        sh '''
          docker --version
          docker build -t ${IMAGE} .
        '''
      }
    }

    stage('Login & Push to Docker Hub') {
      steps {
        script {
          // Use Jenkins credential (username/password) to login and push
          withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
            sh '''
              echo "Logging in to Docker Hub as ${DOCKERHUB_USER}"
              echo "${DOCKERHUB_PASS}" | docker login -u "${DOCKERHUB_USER}" --password-stdin
              docker push ${IMAGE}
              docker logout
            '''
          }
        }
      }
    }
  }

  post {
    success {
      echo "Docker image pushed: ${env.IMAGE}"
    }
    failure {
      echo "Build failed — not pushed"
    }
    always {
      // optional: clean up local images on agent to save disk
      sh '''
        docker rmi ${IMAGE} || true
      '''
    }
  }
}
