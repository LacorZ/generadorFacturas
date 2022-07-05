mvn -U clean package
Copy-Item -Path ".\target\rts-0.0.1-SNAPSHOT-jar-with-dependencies.jar" -Destination ".\distributable" 