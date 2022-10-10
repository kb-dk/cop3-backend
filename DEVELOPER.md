# Developer documentation


## Use a local postgres docker image
Go to src/docker

Build with: docker-compose up -d

Start/stop with: docker-compose start/stop

Connect to database
host: localhost
port: 25432
database: cop
userId: cop
password cop123

To get a fresh database
docker-compose down
docker volume prune -f
docker-compose up -d

## Data migration

For at kunne migrere data fra Oracle til Postgres har vi hbm-filer og java-klasser for Oracle 

hbm-filer og hibernate configuration for Oracle ligger i 
main/resources/oracle

Oracle java klasser ligger i 
main/java/dk/kb/cop3/backend/migrate/hibernate





