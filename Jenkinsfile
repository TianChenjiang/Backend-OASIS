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
      environment {
        ESHOST='172.16.32.83'
      }

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
          serverImage = docker.build('rubiks-oasis/backend' + ":$BUILD_NUMBER")
          initImage = docker.build('rubiks-oasis/init_es' + ":$BUILD_NUMBER", "./init_es")
        }
      }
    }

    stage('Push Image') {
      steps {
        script {
          docker.withRegistry( registrySite, registryCredential ) {
            serverImage.push()
            // push一次latest标签
            serverImage.push('latest')

            initImage.push()
            initImage.push('latest')
          }
        }

      }
    }

    stage('Remove Image') {
      steps {
        sh "docker rmi rubiks-oasis/backend:$BUILD_NUMBER"
        sh "docker rmi rubiks-oasis/init_es:$BUILD_NUMBER"
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
    registryCredential = 'aliyun'
    serverHost = credentials('greenwood-server-host')
    serverPassword = credentials('greenwood-server-password')
  }
}