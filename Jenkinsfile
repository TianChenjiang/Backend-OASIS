pipeline {
  agent any
  stages {
    stage('Email') {
      steps {
        mail bcc: '', body: "Jenkins build: $BUILD_URL", cc: '', from: 'm15123052642@163.com', replyTo: '', subject: 'Jenkins build', to: '1027572886a@gmail.com'
      }
    }
    stage('build') {
      steps {
        sh 'mvn --version'
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Test') {
      steps {
        sh 'mvn test'
      }

      post {
        always {
          junit 'target/surefire-reports/*.xml'
          jacoco(
                execPattern: 'target/*.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java',
                exclusionPattern: 'src/test*'
          )
        }
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