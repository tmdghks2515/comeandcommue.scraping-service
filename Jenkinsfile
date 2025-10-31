pipeline {
  agent any
  environment {
    REGISTRY    = '192.168.219.113:5000'  // A 서버(사설 레지스트리)
    DEPLOY_HOST = '192.168.219.145'       // B 서버
    DEPLOY_DIR  = '/srv/apps/daneyo'
    SERVICE     = 'scraping-service'
    IMAGE_TAG   = "${env.BUILD_NUMBER}"

    // 빌드 속도/성능 향상
    DOCKER_BUILDKIT = '1'
    COMPOSE_DOCKER_CLI_BUILD = '1'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker Build & Push') {
      steps {
        script {
          def image  = "${REGISTRY}/${SERVICE}:${IMAGE_TAG}"
          def latest = "${REGISTRY}/${SERVICE}:latest"

          // 레지스트리 로그인 (자격증명 아이디를 Jenkins Credentials로 관리 권장)
          sh """
            set -e
            docker login ${REGISTRY} -u ${env.DOCKER_USER:-admin} -p ${env.DOCKER_PASS:-admin}
            docker build --pull -t ${image} -t ${latest} .
            docker push ${image}
            docker push ${latest}
          """
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
    success {
      // dangling 이미지 정리
      sh 'docker image prune -f || true'
    }
  }
}
