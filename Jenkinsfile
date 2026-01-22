pipeline {
	agent any

	environment {
		MAVEN_OPTS = '-Xmx1024m'
	}

	stages {
		stage('Checkout') {
			steps {
				git(
					branch: 'master',
					url: 'https://github.com/3ga01/shiny-eureka',
					credentialsId: 'test' // Jenkins credential ID
				)
			}
		}

		stage('Build & Test') {
			steps {
				sh 'mvn clean verify'
			}
		}

		stage('Publish JaCoCo Report') {
			steps {
				jacoco(
					execPattern: '**/target/jacoco.exec',
					classPattern: '**/target/classes',
					sourcePattern: '**/src/main/java',
					exclusionPattern: '**/dto/**,**/config/**'
				)
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
			archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
			junit 'target/surefire-reports/*.xml'
		}
	}
}
