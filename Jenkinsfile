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
