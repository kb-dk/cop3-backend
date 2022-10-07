# Developer documentation


## Use a local postgres docker image
Go to src/docker

Build with: docker-compose up --no-start

Start/stop with: docker-compose start/stop

Connect to database
host: localhost
port: 25432
database: cop
userId: cop
password cop123

To get a fresh database
docker-compose stop
docker-compose down
docker volume prune (to clean up)
docker-compose up --no-start


