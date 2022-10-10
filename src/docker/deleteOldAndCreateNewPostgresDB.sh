#Deletes old postgres docker-container and creates a new
#Use if you want new clean and shiny postgres instanse
docker-compose down
docker volume prune -f
docker-compose up -d
