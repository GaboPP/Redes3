run:
	mvn exec:java -Dexec.mainClass="Servidor.multiserver"

compile:
	mvn install
	mvn compile
	mvn package