[![Build Status](https://dev.azure.com/FabianGirgert/my-owntracks-recorder/_apis/build/status/cryxy.my-owntracks-recorder?branchName=master)](https://dev.azure.com/FabianGirgert/my-owntracks-recorder/_build/latest?definitionId=2?branchName=master)
# my-owntracks-recorder
Store and access data published by OwnTracks apps in InfluxDB. It supports offline reverse geocoding, geohashing, and GPX export (REST API). 

## Architecture
![architecture of my-owntracks-recorder](docs/my-owntracks-recorder-architecture.png)

## Grafana Integration
Use [grafana-trackmap-panel](https://github.com/pR0Ps/grafana-trackmap-panel) to visualize your GPS points on a map in Grafana. 

![my-owntracks-recorder Grafana integration](docs/my-owntracks-recorder-grafana-integration.png)

## System requirements
- OwnTracks app (Android / iOS)
- MQTT broker (Mosquitto, ...)
- InfluxDB
- Docker or JRE (> 1.8)
- Grafana (optional visualization)

## Getting started

### Create InfluxDB Database and Users
```bash
$ influx
Connected to http://localhost:8086 version 0.13
InfluxDB shell version: 0.13
> CREATE DATABASE owntracks_db
> CREATE USER owntracks WITH PASSWORD 'AnotherSuperbPassword456-'
> CREATE USER grafana WITH PASSWORD 'PleaseLetMeRead789?'
> GRANT ALL ON owntracks_db TO owntracks
> GRANT READ ON owntracks_db TO grafana
> exit
```

### Configuration
- create a `config` folder
- download one or more placenames file from http://download.geonames.org/export/dump/ for performing reverse geocoding
- customize the `config.properties`:

```
# MQTT-Server
de.cryxy.owntracks.recorder.mqtt.server.uri = ssl://domain.de
de.cryxy.owntracks.recorder.mqtt.server.user.name = 
de.cryxy.owntracks.recorder.mqtt.server.user.password = 
de.cryxy.owntracks.recorder.mqtt.server.topic = 

# Geohashing 
# https://gis.stackexchange.com/questions/115280/what-is-the-precision-of-a-geohash
de.cryxy.owntracks.recorder.processing.geohashCharacterPrecision=6
# Geocoding
# http://download.geonames.org/export/dump/
de.cryxy.owntracks.recorder.processing.geonames.zips=config/DE.zip,config/RE.zip
# Only major city names?
de.cryxy.owntracks.recorder.processing.geonames.onlyMajorCities=true

# InfluxDB
de.cryxy.owntracks.recorder.influxdb.url = 
de.cryxy.owntracks.recorder.influxdb.dbName = 
de.cryxy.owntracks.recorder.influxdb.user.name = 
de.cryxy.owntracks.recorder.influxdb.user.password = 

# API
de.cryxy.owntracks.recorder.api.user.name = 
de.cryxy.owntracks.recorder.api.user.password = 
```

### Run it with docker as system.d service
It is assumed that your config folder is located under `/opt/owntracksrecorder/config`
 
Place this `owntracksrecorder.service` file into `/etc/systemd/system`.

```
[Unit]
Description=My Owntracks-Recorder
Requires=docker.service
After=docker.service

[Service]
Restart=always
ExecStart=/usr/bin/docker run --name=%n \
  -v /etc/localtime:/etc/localtime:ro \
  -v /etc/timezone:/etc/timezone:ro \
  -v /opt/owntracksrecorder/config:/start/config:ro \
  -p 8090:8080 \
  -e RECORDER_CONFIG=/start/config/config.properties \
  cryxy/my-owntracks-recorder:1.0.0-snapshot
ExecStop=/usr/bin/docker stop -t 2 %n ; /usr/bin/docker rm -f %n

[Install]
WantedBy=multi-user.target
```


Then run `sudo systemctl enable owntracksrecorder.service`.

Finally run `sudo systemctl start owntracksrecorder.service` to start my-owntracks-recorder running.

### Run it without docker
- check out the project
- build the project `mvn clean install`
- cd `my-owntracks-recorder-app`
- start the application from the `target` folder with `java -jar my-owntracks-recorder-app.jar`

## API for GPX Export
The REST-API is secured using Basic-Auth. Use the credentials you provided in the `config.properties`. 
 
```
POST /api/locations/query HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Basic xyz=

{"startDate": "2018-11-10T00:00:00", "endDate":"2018-11-24T00:00:00","userName":"cryxy"}
```

More query options are available. See de.cryxy.owntracks.recorder.influxdb.LocationQuery.