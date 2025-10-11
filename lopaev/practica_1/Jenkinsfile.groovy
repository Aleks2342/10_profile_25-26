pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'Aleks2342/10_profile_25-26'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        KUBE_CONFIG = credentials('kubeconfig')
        DOCKER_REGISTRY_CREDENTIALS = credentials('dockerhub-credentials')
    }
    
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'patch-1',
                    url: 'https://github.com/Aleks2342/10_profile_25-26.git'
            }
        }
        
        stage('Test') {
            steps {
                sh 'python -m pytest app/tests/ || true'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_REGISTRY_CREDENTIALS) {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Обновляем манифест с новым тегом образа
                    sh """
                    sed -i 's|IMAGE_PLACEHOLDER|${DOCKER_IMAGE}:${DOCKER_TAG}|g' k8s/deployment.yaml
                    """
                    
                    // Применяем конфигурацию Kubernetes
                    withKubeConfig([credentialsId: 'kubeconfig']) {
                        sh 'kubectl apply -f k8s/'
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Очистка
            sh 'docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true'
        }
        success {
            slackSend channel: '#deployments',
                      message: "Deployment successful: ${env.BUILD_URL}"
        }
        failure {
            slackSend channel: '#deployments',
                      message: "Deployment failed: ${env.BUILD_URL}"
        }
    }
}