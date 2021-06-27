#export EZYFOX_SERVER_HOME=
mvn -pl . clean install
mvn -pl breaking-game-common -Pexport clean install
mvn -pl breaking-game-app-api -Pexport clean install
mvn -pl breaking-game-app-entry -Pexport clean install
mvn -pl breaking-game-plugin -Pexport clean install