pipeline {
  agent any

  environment {
    DOCKERHUB_REPO = "bayarmaa/jenkins-demo"   // <-- change to your repo if different
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    IMAGE = "${DOCKERHUB_REPO}:${IMAGE_TAG}"
    CREDENTIALS_ID = "dockerhub-credentials"   // <-- use the Jenkins credential id you have
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Show files') {
      steps {
        echo "Workspace contents:"
        sh 'pwd; ls -la || true'
      }
    }

    stage('Build Docker image') {
      steps {
        script {
          echo "Building Docker image ${IMAGE}"
          // detect buildx availability and choose method
          def buildCmd = """
            set -e
            echo "docker version:"
            docker --version || true

            echo "Checking for docker buildx..."
            if docker buildx version >/dev/null 2>&1; then
              echo "buildx available. Using buildx (BuildKit)."
              # ensure builder exists and use default builder — create a builder if none
              if ! docker buildx inspect default >/dev/null 2>&1; then
                docker buildx create --use --name jenkins-builder || true
              else
                docker buildx use default || true
              fi
              # build with buildx and load into local docker (so we can push)
              docker buildx build --load -t ${IMAGE} .
            else
              echo "buildx not available. Falling back to legacy docker build (disabling BuildKit)."
              export DOCKER_BUILDKIT=0
              docker build -t ${IMAGE} .
            fi
          """
          sh buildCmd
        }
      }
    }

    stage('Login & Push to Docker Hub') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: "${env.CREDENTIALS_ID}", usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
            sh '''
              set -e
              echo "Logging in to Docker Hub as ${DOCKERHUB_USER}"
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
      sh 'docker rmi ${IMAGE} || true'
    }
  }
}
