#cd /usr/app/backend-oasis || exit

# 主要是拉取docker-compose.yml
# git pull origin develop

#docker-compose down
#docker-compose pull
#docker-compose up -d

#docker-compose stop server
#docker-compose pull server
#docker-compose up -d server

docker pull registry.cn-hangzhou.aliyuncs.com/rubiks-oasis/backend
docker stop backend-oasis
docker rm backend-oasis
docker run -d --network="host" \
--name=backend-oasis \
--restart=always \
-p 8081:8081 \
--env MONGO=mongodb://greenwood:2020liujia@localhost:27017 \
--env ESHOST=localhost \
registry.cn-hangzhou.aliyuncs.com/rubiks-oasis/backend