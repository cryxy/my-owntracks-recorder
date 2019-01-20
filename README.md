[![Build Status](https://dev.azure.com/FabianGirgert/my-owntracks-recorder/_apis/build/status/cryxy.my-owntracks-recorder?branchName=master)](https://dev.azure.com/FabianGirgert/my-owntracks-recorder/_build/latest?definitionId=2?branchName=master)
# my-owntracks-recorder
Store and access data published by OwnTracks apps in InfluxDb.


## Run it with docker as system.d service
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

```bash
service owntracksrecorder start
```

## API for GPX Export
```
POST /api/locations/query HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Basic xyz=

{"startDate": "2018-11-10T00:00:00", "endDate":"2018-11-24T00:00:00","userName":"cryxy"}
``` 