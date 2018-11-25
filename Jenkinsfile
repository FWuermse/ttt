pipeline {
    agent any
    stages {
        stage('Create folder') {
            steps {
                sh 'mkdir build'
            }
        }
        stage('Test') {
            steps {
                git branch: 'master',
                    credentialsId: '39620-1176-3956-df44-012855',
                    url: 'ssh://git@bitbucket.org:FWuermse/totalterminationtournamentapi_1.0.git'
            }
        }
        stage('Deploy') {
            steps {
               sh 'comming ssoon'
            }
        }
    }
}
