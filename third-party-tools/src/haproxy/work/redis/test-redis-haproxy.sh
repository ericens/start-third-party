#!/usr/bin/env bash
# 安装
brew install haproxy

# 启动
haproxy -f ./redis.cfg -d

# 服务端口测试
telnet localhost 33210
redis-cli -p 33210
    # 关闭1个redis-server ，验证client 查询情况
    # 关闭2个redis-server ，验证client 查询情况

# 登录查看状态服务器
http://localhost:8888/haproxy-status

# 启动java程序，看到测试

#关闭
ps -ef  |grep haproxy
kill -9 33145