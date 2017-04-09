# -*- coding: utf-8 -*-

#nohup python /home/user/ABInBev/envABINBev/Source/main.py &
import nvector as nv
import logging
import requests
import operator
import os
import sqlite3
import json
from bottle import Bottle, route, run, request, get
from geojson import Feature, FeatureCollection, Point
from math import degrees,radians
from datetime import datetime

#########################################################################################################

GOOGLE_API_KEY = 'AIzaSyAKfqy49Gp-b9BnTVndMaYPFR9Aj-kqRDk' #ezequiel.grondona@gmail.com
GOOGLE_SHORTURL_KEY = 'AIzaSyAP5ZuaP739OjM7wMYoVV13sa48QNfAMHI'
#GOOGLE_API_KEY = 'AIzaSyBIeTSoSFL84r7D-MRjGlkjHYFFAxiielY ' #egrondona.fc@gmail.com
GOOGLE_SHORTURL_URL = 'https://www.googleapis.com/urlshortener/v1/url'
GOOGLE_GEOCODE_URL = 'https://maps.googleapis.com/maps/api/geocode/json'
GOOGLE_PLACES_URL = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json'
GOOGLE_MAPS_URL = 'http://maps.google.com.ar/?q={0}'
GOOGLE_PLACES_RADIUS = 800
GOOGLE_PLACES_RADIUS_DELTA = 500

SERVER_IP = '0.0.0.0'
SERVER_PORT = 8080
SERVER_BOT_POST_ROUTE = '/abInBev/botPost'
SERVER_DUMMY_ROUTE = '/abInBev/dummyPost'
SERVER_STATIC_PATH = os.path.dirname(os.path.abspath(__file__)) + '/web/static'

LOG_FORMAT = '%(asctime)-15s %(message)s'

#########################################################################################################

logging.basicConfig(format=LOG_FORMAT)
logger = logging.getLogger('main_log')
logger.setLevel('DEBUG')


wgs84 = nv.FrameE(name='WGS84')


patagonia_bars_file = open('patagonia.json','r',encoding='UTF-8')
patagonia_bars = json.loads(patagonia_bars_file.read())
patagonia_bars_file.close()
bars_list = [{'name': bar.get('name'), 
        'pos': bar.get('geometry').get('location'),
        'rating': int(bar.get('rating',0)),
        'vicinity': bar.get('vicinity'),
        'url': bar.get('shortUrl')}
        for bar in patagonia_bars['results']]


db_conn = sqlite3.connect('data.db')
db_cur = db_conn.cursor()

#########################################################################################################

def sqlExecuteAndCommit(cursor,connection,sentence):
    cursor.execute(sentence)
    connection.commit()

def createJs(usrsList,midpointPos,barPos):
    featuresList = list()
    for usr in usrsList:
        featuresList.append(Feature(geometry=Point((usr['lon'], usr['lat'])),
            properties={'kind': 'user','name': usr['name']}))

    #featuresList.append(Feature(geometry=Point((midpointPos['pos']['lng'], midpointPos['pos']['lat'])),
    #        properties={'kind': 'midpoint','name': midpointPos['name'],'url': midpointPos['url']}))
    featuresList.append(Feature(geometry=Point((midpointPos['lon'], midpointPos['lat'])),
            properties={'kind': 'midpoint'}))

    featuresList.append(Feature(geometry=Point((barPos['pos']['lng'], barPos['pos']['lat'])),
            properties={'kind': 'bar','name': barPos['name'],'address': barPos['vicinity'],'url': barPos['url']}))

    return str(FeatureCollection(featuresList))

def getDistance(point1,point2):
    point1 = wgs84.GeoPoint(point1.get('lat'), point1.get('lng'), degrees=True)
    point2 = wgs84.GeoPoint(point2.get('lat'), point2.get('lng'), degrees=True)
    s_12, _azi1, _azi2 = point1.distance_and_azimuth(point2)
    return s_12

def getMiddlePointFromList(positions):
    n_EM_E = nv.unit(sum(positions)) 
    lat, lon = nv.n_E2lat_lon(n_EM_E)
    lat, lon = degrees(lat[0]),degrees(lon[0])

    return lat,lon

def getShortURL(longURL):
    payload = {'key': GOOGLE_SHORTURL_KEY}
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    try:
        r = requests.post(GOOGLE_SHORTURL_URL, json={'longUrl': longURL}, headers=headers, params=payload)
    except:
        return longURL

    return r.json().get('id',longURL)

def getLocationName(lat,lon):
    while (True):
        payload = {'key': GOOGLE_API_KEY, 'latlng': '{0},{1}'.format(lat,lon), 'language': 'es'}
        try:
            r = requests.get(GOOGLE_GEOCODE_URL, params=payload, timeout=3)
            if (r.status_code == 200): break
        except:
            logger.debug('getLocationName: ERROR')
    
    midpoint = r.json().get('results')[0]
    midpoint = {'name': midpoint.get('formatted_address'), 
                'pos': midpoint.get('geometry').get('location'),
                'url': getShortURL(GOOGLE_MAPS_URL.format(midpoint.get('formatted_address')))}

    logger.debug('getLocationName: Midpoint is %s',midpoint.get('name'))

    return midpoint

def getGoogleNearbyBars(lat,lon):
    radius = GOOGLE_PLACES_RADIUS

    while (True): 
        payload = {'key': GOOGLE_API_KEY, 'location': '{0},{1}'.format(lat,lon), 'radius': radius,'types': 'bar'}
        try:
            r = requests.get(GOOGLE_PLACES_URL, params=payload)
            bars_list = r.json()['results']
            if (len(bars_list) > 0): break
            else: radius = radius + GOOGLE_PLACES_RADIUS_DELTA
            logger.debug('+{0}mts'.format(GOOGLE_PLACES_RADIUS_DELTA))
        except:
            logger.debug('getGoogleNearbyBars: ERROR')

    bars_list = [{'name': bar.get('name'), 
                'pos': bar.get('geometry').get('location'),
                'rating': int(bar.get('rating',0)),
                'vicinity': bar.get('vicinity'),
                'distance': getDistance({'lat':lat,'lng':lon},bar.get('geometry').get('location'))}
                for bar in bars_list]

    bars_list = sorted(bars_list, key=operator.itemgetter('rating'), reverse=True)
    bars_list = bars_list[0:5]
    for bar in bars_list:
        bar['url'] = getShortURL(GOOGLE_MAPS_URL.format(bar.get('name')+', '+bar.get('vicinity')))

    logger.debug('getGoogleNearbyBars: Found %s bars in %s mts radius',len(bars_list),radius)
    logger.debug('getGoogleNearbyBars: %s',bars_list)

    return bars_list

def getPatagoniaNearbyBars(lat,lon):
    minimum_distance = 1000000 #1M mts
    for bar in bars_list:
        bar['distance'] = getDistance({'lat':lat,'lng':lon},bar.get('pos'))
        #print(bar.get('name')+' - ' +str(bar['distance']))
        if (bar['distance'] < minimum_distance):
            minimum_distance = bar['distance']
            chosenBar = bar

    logger.debug('getPatagoniaNearbyBars: %s',[chosenBar])

    return [chosenBar]

#########################################################################################################

# Creates de web app
listener = Bottle()

#########################################################################################################

@listener.route(SERVER_BOT_POST_ROUTE, method='POST')
def botPost():
    positions = list()

    try:
        users_data = request.json['usersLocationList'] 

    except:
        return 'Ups, algo salió mal'


    if (len(users_data) <= 1): 
        return 'Ups, parece que estás solo'

    else:
        for pos in users_data:
            positions.append(nv.lat_lon2n_E(radians(float(pos.get('lat'))),radians(float(pos.get('lon')))))

        lat, lon = getMiddlePointFromList(positions)
        #location = getLocationName(lat,lon)
        bars = getPatagoniaNearbyBars(lat,lon)
        
        text = 'Encontrémonos en _{0}_ ({1}) a disfrutar una rica *Cerveza Patagonia*. ¡Nos queda cómodo a todos!'.format(bars[0]['name'],bars[0]['url'])

        logger.debug('botPost: %s',text)
        
        geoJsonData = createJs(users_data,{'lat':lat,'lon':lon},bars[0])
        db_cur.execute("INSERT INTO usrQuery(dateQuery,jsonQuery,jsonResp,groupSize,barName) VALUES (?,?,?,?,?)",[str(datetime.now()),str(users_data),str(geoJsonData),len(users_data),bars[0]['name']])
        db_conn.commit()
        
        return text

@listener.route(SERVER_DUMMY_ROUTE, method='POST')
def dummyPost():
    return request.json

#########################################################################################################

run(listener,host=SERVER_IP, port=SERVER_PORT)