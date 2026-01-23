pipeline {
	agent {
		docker {
			image 'maven:3.9.6-eclipse-temurin-21'
			args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.m2:/root/.m2'
		}
	}

	environment {
		MAVEN_OPTS = '-Xmx1024m'
	}

	stages {
		stage('Build & Test') {
			steps {
				sh 'mvn clean verify'
			}
		}

		stage('Package') {
			steps {
				sh 'mvn package -DskipTests'
			}
		}
	}

	post {
		always {
			junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
			archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
		}
	}
}
