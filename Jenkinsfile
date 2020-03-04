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
        script {
          serverImage = docker.build(serverName + ":$BUILD_NUMBER")
//           dbImage = docker.build(dbName + ":$BUILD_NUMBER", "./mongo")
        }
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

    stage('Push Image') {
      steps {
        script {
          docker.withRegistry( registrySite, registryCredential ) {
            serverImage.push()
            serverImage.push('latest')
//             dbImage.push()
//             dbImage.push('latest')

          }
        }

      }
    }

    stage('Remove Image') {
      steps {
        sh "docker rmi " + serverName + ":$BUILD_NUMBER"
//         sh "docker rmi " + dbName + ":$BUILD_NUMBER"
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
    serverName = 'rubiks-oasis/backend'
    dbName = 'rubiks-oasis/mongodb'
    serverHost = credentials('greenwood-server-host')
    serverPassword = credentials('greenwood-server-password')
  }
}