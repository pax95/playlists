playlists
=========

# Playlists mashup example showing real time music broadcasts on DR (Danish broadcasting corporation) channels.
Example is implemented using WebSockets HTML 5 with Camel, ActiveMQ and Jetty.

## Camel and ActiveMQ

1) Download and unzip ActiveMQ 5.6.0 from http://activemq.apache.org/download.html  

2) start ActiveMQ from the bin directory with ./activemq console

3)  Compile and start playlists-websocket application

    cd playlists/playlists-websocket
    mvn clean camel:run

4)  Compile and start playlists-web application

    cd playlists/playlists-web
    mvn clean package jetty:run

5) Open your web browser

    http://localhost:8080/playlists.html
    and click on connect button

6) 	Compile and start playlists-feeder application
	
	cd playlists/playlists-feeder
	mvn clean camel:run -D lfm-api-key=your-LastFmApiKey
    
A api key can be obtained from http://www.last.fm/api/account. 
Without a valid key the playlists will not be enriched with album info from Last FM.

