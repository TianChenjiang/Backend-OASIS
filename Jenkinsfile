// 声明式pipeline
pipeline {
  environment {
    // 阿里云容器镜像
    registrySite = 'https://registry-vpc.cn-hangzhou.aliyuncs.com'
    // 所构建的容器名
    registry = "rubiks-oasis/backend"
    // jenkins中事先声明好的凭证Credential
    registryCredential = 'aliyun'
    // jenkins中事先声明好的服务器Host
    serverHost = credentials('greenwood-server-host')
    // jenkins中事先声明好的服务器密码
    serverPassword = credentials('greenwood-server-password')
  }
  // agent需设置为any，不能在agent docker中触发docker构建
  agent any
  // 声明依赖工具，每次构建的maven会缓存所需依赖
  tools {
    maven 'maven3'
  }

  stages {
    stage('build') {
      steps {
        sh 'mvn --version'
        // 生成jar包到target
        sh 'mvn -B -DskipTests clean package'
      }
    }
    stage('Test') {
//       environment {
//         SPRING_PROFILES_ACTIVE = 'test'
//       }
      steps {
        sh 'mvn test'
      }
      // 生成测试报告
      post {
          always {
              junit 'target/surefire-reports/*.xml'
          }
      }
    }
    stage('Build Image') {
      steps {
        script {
          // 切换到 命令式脚本
          // 使用build阶段生成的jar包来构建容器，需要Docker插件
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('Push Image') {
      steps{
        script {
          // 设置阿里云容器镜像Hub服务
          docker.withRegistry( registrySite, registryCredential ) {
            dockerImage.push()
            // push一次latest标签
            dockerImage.push('latest')
          }
        }
      }
    }

    stage('Remove Image') {
          steps{
            sh "docker rmi $registry:$BUILD_NUMBER"
          }
    }

    stage('SSH Deploy') {
        steps {
          script {
            // 切换到 命令式脚本
            // 设置remote服务器，需要插件ssh-agent
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
}