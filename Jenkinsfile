pipeline {
	agent any
		stages {
			stage('Kill old server') {
				steps {
					sh '''cd backend/rocket_beach/
						kill -9 `lsof -n -i :3000 | grep LISTEN | cut -f 5 -d ' '`
						'''
				}
			}
			stage('Start new server') {
				steps {
					sh '''cd backend/rocket_beach/
						rails s -d -b 0.0.0.0 2>&1 > /home/spaceapps/jenkins.log
						'''
				}
			}
		}
}
