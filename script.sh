docker rm -f ncov-stats
docker rmi -f $(docker images |grep "ncov-stats" | awk '{print $3}')
docker build -t ncov/ncov-stats:1.0 .
docker run -m 800m \
           -d -p 10001:10001 \
           -v /etc/timezone:/etc/timezone \
           -v /etc/localtime:/etc/localtime \
           -v /home/admin/ncov/ncov-stats/log:/root/ncov-stats/target/log \
           --name ncov-stats ncov/ncov-stats:1.0
