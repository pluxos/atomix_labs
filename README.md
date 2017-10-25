# Atomix Labs
Conjunto de exercicios usando o framework Atomix.

## Change log
Exercicio usando copycat versao 1.1.4. Em breve espero atualiza-lo para atomix-raft.

# Como usar
## Build
git clone https://github.com/pluxos/atomix_labs

cd atomix_labs

cd replication

mvn compile

mvn test

## Run
### Servers
mvn exec:java -Dexec.mainClass="atomix_lab.state_machine.server.GraphStateMachine" -Dexec.args="0 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002"

mvn exec:java -Dexec.mainClass="atomix_lab.state_machine.server.GraphStateMachine" -Dexec.args="1 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002"

mvn exec:java -Dexec.mainClass="atomix_lab.state_machine.server.GraphStateMachine" -Dexec.args="2 127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002"

### Clients
mvn exec:java -Dexec.mainClass="atomix_lab.state_machine.client.GraphClient" -Dexec.args="127.0.0.1 5000 127.0.0.1 5001 127.0.0.1 5002"
