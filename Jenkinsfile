pipeline {
    agent {
        label "basic-ubuntu"
    }
    tools {
        maven 'apache-maven-latest'
        jdk 'openjdk-jdk17-latest'
    }
    options {
      timeout(time: 60, unit: 'MINUTES')
      disableConcurrentBuilds()
    }
    stages {
        stage('Main') {
            steps {
                sh 'mvn -B -f parent/pom.xml clean verify -DskipTests'
            }
        }
    }
}
