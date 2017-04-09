# -*- coding: utf-8 -*-

#nohup python /home/user/ABInBev/envABINBev/Source/main.py &
import logging
import requests
import jinja2
import os
import sqlite3
from bottle import Bottle, route, run, request, get, static_file

#########################################################################################################

SERVER_IP = '0.0.0.0'
SERVER_PORT = 8081
SERVER_WEB_LAST_ROUTE = '/abInBev/webGetLast'
SERVER_WEB_BYID_ROUTE = '/abInBev/webGetId'
SERVER_STATIC_PATH = os.path.dirname(os.path.abspath(__file__)) + '/web/static'

LOG_FORMAT = '%(asctime)-15s %(message)s'

DB_NAME = 'data.db'

#########################################################################################################

logging.basicConfig(format=LOG_FORMAT)
logger = logging.getLogger('main_log')
logger.setLevel('DEBUG')

env = jinja2.Environment(
    loader=jinja2.FileSystemLoader('web'),
    autoescape=True,
)

db_conn = sqlite3.connect(DB_NAME)
db_cur = db_conn.cursor()

def sqlExecuteAndFetch(cursor,sentence):
    cursor.execute(sentence)
    return cursor.fetchone()

def sqlExecuteAndCommit(cursor,connection,sentence):
    cursor.execute(sentence)
    connection.commit()

#########################################################################################################

# Creates de web app
listener = Bottle()

#########################################################################################################

# Static Routes
@listener.route("/static/<filepath:path>")
def server_static(filepath):
    return static_file(filepath, root=SERVER_STATIC_PATH)

@listener.route(SERVER_WEB_LAST_ROUTE, method='GET')
def websiteGetLast():
    jinjaTemplate = env.get_template('index.html')
    geoJSON = sqlExecuteAndFetch(db_cur,'SELECT jsonResp FROM usrQuery ORDER BY id DESC LIMIT 1')
    print(geoJSON[0])

    meanSize = sqlExecuteAndFetch(db_cur,'SELECT avg(groupSize) FROM usrQuery')
    queriesLastDay = sqlExecuteAndFetch(db_cur,'SELECT COUNT(*) FROM usrQuery GROUP BY date(dateQuery) ORDER BY date(dateQuery) DESC LIMIT 1')
    mostRecomended = sqlExecuteAndFetch(db_cur,'SELECT barName,COUNT(*) FROM usrQuery GROUP BY barName ORDER BY COUNT(*) DESC LIMIT 1')
    totalQueries = sqlExecuteAndFetch(db_cur,'SELECT COUNT(1) FROM usrQuery')


    return jinjaTemplate.render(geoJsonData=geoJSON,
        meanSize=meanSize,
        queriesLastDay=queriesLastDay,
        mostRecomended=mostRecomended,
        totalQueries=totalQueries)


@listener.route(SERVER_WEB_BYID_ROUTE, method='GET')
def websiteGetById(id=1):
    jinjaTemplate = env.get_template('index.html')
    queryId = int(request.query.id)

    geoJSON = sqlExecuteAndFetch(db_cur,'SELECT jsonResp FROM usrQuery WHERE id = %s' % queryId)
    
    meanSize = sqlExecuteAndFetch(db_cur,'SELECT avg(groupSize) FROM usrQuery')
    queriesLastDay = sqlExecuteAndFetch(db_cur,'SELECT COUNT(*) FROM usrQuery GROUP BY date(dateQuery) ORDER BY date(dateQuery) DESC LIMIT 1')
    mostRecomended = sqlExecuteAndFetch(db_cur,'SELECT barName,COUNT(*) FROM usrQuery GROUP BY barName ORDER BY COUNT(*) DESC LIMIT 1')
    totalQueries = sqlExecuteAndFetch(db_cur,'SELECT COUNT(1) FROM usrQuery')

    print(geoJSON[0])
    return jinjaTemplate.render(geoJsonData=geoJSON,
        meanSize=meanSize,
        queriesLastDay=queriesLastDay,
        mostRecomended=mostRecomended,
        totalQueries=totalQueries)

#########################################################################################################

run(listener,host=SERVER_IP, port=SERVER_PORT)