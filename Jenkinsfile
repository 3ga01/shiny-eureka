pipeline {
	agent any

	tools {
		jdk 'jdk-21'
		maven 'maven-3'
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
