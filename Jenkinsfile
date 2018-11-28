pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                sh 'echo Checkout successful'
            }
        }
        stage('Build') {
            steps {
                sh 'sudo docker stop ttt_api && sudo docker rm ttt_api'
                sh 'sudo docker rmi ttt_api'
                sh 'sudo docker build -t ttt_api .'
            }
        }
        stage('Deploy') {
            steps {
               sh 'sudo docker-compose up -d'
            }
        }
    }
}
