pipeline {
  agent any
  environment {
    REGISTRY    = '192.168.219.113:5000'  // A서버(사설 레지스트리)
    DEPLOY_HOST = '192.168.219.145'       // B서버
    DEPLOY_DIR  = '/srv/apps/daneyo'
    SERVICE     = 'scraping-service'      // 각 서비스마다 변경
    IMAGE_TAG   = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker Build & Push') {
      steps {
        script {
          def image = "${REGISTRY}/${SERVICE}:${IMAGE_TAG}"
          def latest = "${REGISTRY}/${SERVICE}:latest"

          withCredentials([string(credentialsId: 'GITHUB_PKG_TOKEN', variable: 'GPR_KEY')]) {
            sh """
              DOCKER_BUILDKIT=0 docker build \
              --build-arg GITHUB_ACTOR=tmdghks2515 \
              --build-arg GITHUB_TOKEN=${GPR_KEY} \
                -t ${image} -t ${latest} .
              docker push ${image}
              docker push ${latest}
            """
          }
        }
      }
    }

    stage('Deploy') {
      steps {
        sh """
          ssh -o StrictHostKeyChecking=no ubuntu@${DEPLOY_HOST} '
            set -e
            cd ${DEPLOY_DIR}
            sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=${IMAGE_TAG}/" .env
            docker compose --env-file .env pull ${SERVICE}
            docker compose --env-file .env up -d --no-deps ${SERVICE}
          '
        """
      }
    }
  }

  post {
    success { sh 'docker image prune -f' }
  }
}
