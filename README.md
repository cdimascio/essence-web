# essence-web

ic cf push essence -p build/distributions/essence-web-0.0.1.zip -b https://github.com/cloudfoundry/java-buildpack.git


## build

- cd client
- npm run build
- cd ..
- ./gradlew clean build distZip
