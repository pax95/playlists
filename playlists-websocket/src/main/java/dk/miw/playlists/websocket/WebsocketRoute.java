package dk.miw.playlists.websocket;

import org.apache.camel.builder.RouteBuilder;

public class WebsocketRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("activemq:topic:playlists")
			.log("message was ${body}")
			.to("websocket:playlists?sendToAll=true");

	}

}
