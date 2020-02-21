pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn --version'
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Test') {
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }

      }
      steps {
        sh 'mvn test'
      }
    }

    stage('Build Image') {
      steps {
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }

      }
    }

    stage('Push Image') {
      steps {
        script {
          docker.withRegistry( registrySite, registryCredential ) {
            dockerImage.push()
            // push一次latest标签
            dockerImage.push('latest')
          }
        }

      }
    }

    stage('Remove Image') {
      steps {
        sh "docker rmi $registry:$BUILD_NUMBER"
      }
    }

    stage('SSH Deploy') {
      steps {
        script {
          remote = [:]
          remote.name = "greenwood-server"
          remote.host = serverHost
          remote.user = 'root'
          remote.password = serverPassword
          remote.allowAnyHosts = true

          sshScript remote: remote, script: 'deploy.sh'
        }

      }
    }

    stage('Email') {
      steps {
        mail bcc: '', body: 'Jenkins build: ${env.BUILD_URL}', cc: '', from: '', replyTo: '', subject: 'Jenkins build', to: '1027572886a@gmail.com'
      }
    }

  }
  tools {
    maven 'maven3'
  }
  environment {
    registrySite = 'https://registry-vpc.cn-hangzhou.aliyuncs.com'
    registry = 'rubiks-oasis/backend'
    registryCredential = 'aliyun'
    serverHost = credentials('greenwood-server-host')
    serverPassword = credentials('greenwood-server-password')
  }
}