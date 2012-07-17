package dk.miw.playlists;

import java.io.File;
import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;

import dk.miw.playlists.model.Track;

public class PlaylistsFeederRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("timer://pollingTimer?fixedRate=true&delay=0")
			.split().method(ChannelList.class)
			.setHeader(Exchange.HTTP_URI, simple("http://www.dr.dk/playlister/feeds/nowNext/nowPrev.drxml?items=0&cid=${body}"))
			.to("http://dummy").id("dummy")
			.unmarshal().json(JsonLibrary.Gson)
			.filter(simple("${body[now][status]} == 'music'"))
			.convertBodyTo(Track.class)
			.setHeader("artist", simple("${body.artist}"))
			.setHeader("title", simple("${body.title}"))
			.idempotentConsumer(simple("${body.time}-${body.channel}"), FileIdempotentRepository.fileIdempotentRepository(new File("repo.dat"), 250))
			.to("direct:process");
		
		from("direct:process")
			.enrich("direct:lastfmEnricher", new LastFmAggregationStrategy())
			.marshal().json(JsonLibrary.Gson, Track.class)
			.to("activemq:topic:playlists");
			
		//enrich flight with weather information
    	from("direct:lastfmEnricher")
    		.onException(Exception.class)
    			.to("log:dk.miw.playlists?showAll=true&multiline=true" )
    			.handled(true)
    		.end()
    		.removeHeader(Exchange.HTTP_URI)
    		.process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					String artist = exchange.getIn().getHeader("artist", String.class);
					if (artist != null) {
						exchange.getIn().setHeader("artist", URLEncoder.encode(artist, "UTF-8"));
					}
					String title = exchange.getIn().getHeader("title", String.class);
					if (title != null) {
						exchange.getIn().setHeader("title", URLEncoder.encode(title, "UTF-8"));
					}
				}
			})
			.setProperty(Exchange.CHARSET_NAME, constant("UTF-8"))
    		.setHeader(Exchange.HTTP_QUERY, simple("method=track.getInfo&api_key={{lfm-api-key}}&artist=${header.artist}&track=${header.title}"))
			.setBody().simple("1")
			.to("http://ws.audioscrobbler.com/2.0/?throwExceptionOnFailure=false");
	}

}
