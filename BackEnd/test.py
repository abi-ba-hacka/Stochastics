# -*- coding: utf-8 -*-

import requests
from random import randrange, uniform
import time

SERVER_IP = 'localhost'
SERVER_PORT = 8080
SERVER_BOT_POST_ROUTE = '/abInBev/botPost'

LAT_MIN = -34.57
LAT_MAX = -34.61 
LON_MIN = -58.37
LON_MAX = -58.46

#########################################################################################################

def sendRandomRequest():
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}

    usersLocationList = list()

    for i in range(0,randrange(2,5)):
        usersLocationList.append({"lat": float(uniform(LAT_MIN,LAT_MAX)), "lon": float(uniform(LON_MIN,LON_MAX)), "name": "user" + str(i)})

    json_data = {"group": 1, "usersLocationList": usersLocationList}

    post_url = 'http://<IP>:<PORT>/<method>'.replace('<IP>',SERVER_IP).replace('<PORT>',str(SERVER_PORT)).replace('/<method>',SERVER_BOT_POST_ROUTE)
    print(json_data)
    r = requests.post(post_url, json=json_data, headers=headers)
    print(r)
    return r

req_ok = 0
req_num = 0
req_blank = 0
while True:
    print('#####################################################')
    req_num = req_num + 1
    req = sendRandomRequest()
    if (req.status_code == 200): req_ok = req_ok + 1
    if (len(req.content) == 0): req_blank = req_blank + 1
    print('Ok: ' + str(req_ok/req_num) + ' | Nulos:' + str(req_blank/req_num))
    time.sleep(randrange(1,10))