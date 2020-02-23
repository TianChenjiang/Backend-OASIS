cd /usr/app/backend-oasis || exit

# 主要是拉取docker-compose.yml
git pull origin master

docker-compose stop server
docker-compose pull server
docker-compose up -d server