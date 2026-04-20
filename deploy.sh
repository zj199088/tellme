#!/bin/bash

# 健身应用部署脚本
# 适用于CentOS 7.6服务器
# 一键部署前端、后端、Redis和MySQL

set -e

echo "=== 健身应用部署脚本 ==="
echo "开始部署..."

# 1. 安装必要的依赖
echo "\n1. 安装必要的依赖..."

# 更新系统
yum update -y

# 安装EPEL源
yum install -y epel-release

# 安装基础工具
yum install -y wget curl git unzip

# 安装Java 11
yum install -y java-11-openjdk-devel

# 安装Node.js 18
curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
yum install -y nodejs

# 安装Nginx
yum install -y nginx

# 安装Redis
yum install -y redis

# 安装MySQL 8
yum install -y mysql-server mysql

# 2. 构建前端项目
echo "\n2. 构建前端项目..."

# 进入前端目录
cd /workspace/fitness-app-frontend

# 安装依赖
npm install

# 构建项目
npm run build

# 3. 构建后端项目
echo "\n3. 构建后端项目..."

# 进入后端目录
cd /workspace/fitness-app-backend

# 使用Maven构建（如果没有Maven，先安装）
if ! command -v mvn &> /dev/null; then
    echo "安装Maven..."
    wget https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz
    tar -xzf apache-maven-3.8.8-bin.tar.gz
    mv apache-maven-3.8.8 /opt/
    echo 'export PATH=$PATH:/opt/apache-maven-3.8.8/bin' >> /etc/profile
    source /etc/profile
fi

# 构建后端项目
mvn clean package -DskipTests

# 4. 配置Redis
echo "\n4. 配置Redis..."

# 启动Redis服务
systemctl start redis
systemctl enable redis

# 5. 配置MySQL
echo "\n5. 配置MySQL..."

# 启动MySQL服务
systemctl start mysqld
systemctl enable mysqld

# 等待MySQL启动
sleep 5

# 初始化MySQL密码
MYSQL_ROOT_PASSWORD="fitnessapp123"
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD';"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "DELETE FROM mysql.user WHERE User='';"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "DROP DATABASE IF EXISTS test;"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "DELETE FROM mysql.db WHERE Db='test' OR Db='test\_%';"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "FLUSH PRIVILEGES;"

# 创建数据库
mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS fitness_app;"

# 导入初始数据
mysql -u root -p"$MYSQL_ROOT_PASSWORD" fitness_app < /workspace/fitness-app-backend/src/main/resources/init.sql

# 6. 配置后端服务
echo "\n6. 配置后端服务..."

# 创建后端服务目录
mkdir -p /opt/fitness-app/backend

# 复制jar文件到服务目录
cp /workspace/fitness-app-backend/target/fitness-app-backend-*.jar /opt/fitness-app/backend/fitness-app-backend.jar

# 创建systemd服务文件
cat > /etc/systemd/system/fitness-app-backend.service << EOF
[Unit]
Description=Fitness App Backend
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/fitness-app/backend
ExecStart=/usr/bin/java -jar fitness-app-backend.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# 启动后端服务
systemctl daemon-reload
systemctl start fitness-app-backend
systemctl enable fitness-app-backend

# 7. 配置前端服务
echo "\n7. 配置前端服务..."

# 创建前端目录
mkdir -p /opt/fitness-app/frontend

# 复制前端构建结果到服务目录
cp -r /workspace/fitness-app-frontend/dist/* /opt/fitness-app/frontend/

# 创建Nginx配置文件
cat > /etc/nginx/conf.d/fitness-app.conf << EOF
server {
    listen 80;
    server_name _;

    location / {
        root /opt/fitness-app/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

# 测试Nginx配置
nginx -t

# 重启Nginx服务
systemctl restart nginx
systemctl enable nginx

# 8. 检查服务状态
echo "\n8. 检查服务状态..."

echo "\nRedis状态:"
systemctl status redis --no-pager

echo "\nMySQL状态:"
systemctl status mysqld --no-pager

echo "\n后端服务状态:"
systemctl status fitness-app-backend --no-pager

echo "\nNginx状态:"
systemctl status nginx --no-pager

echo "\n=== 部署完成 ==="
echo "前端访问地址: http://服务器IP"
echo "后端API地址: http://服务器IP/api"
echo "MySQL用户名: root"
echo "MySQL密码: fitnessapp123"
echo "Redis端口: 6379"
echo "后端服务端口: 8080"
echo "前端服务端口: 80"
