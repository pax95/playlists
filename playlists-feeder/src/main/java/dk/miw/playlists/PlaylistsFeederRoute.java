package dk.miw.playlists;

import java.io.File;
import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;

public class PlaylistsFeederRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("timer://pollingTimer?fixedRate=true&delay=0")
			.split().method(ChannelList.class)
			.setHeader(Exchange.HTTP_URI, simple("http://www.dr.dk/playlister/feeds/nowNext/nowPrev.drxml?items=0&cid=${body}"))
			.to("http://dummy").id("dummy")
			.unmarshal().json(JsonLibrary.Gson)
			.filter(simple("${body[now][status]} == 'music'"))
			.bean(NowFilter.class)
			.setHeader("artist", simple("${body[track][display_artist]}"))
			.setHeader("title", simple("${body[track][track_title]}"))
			.idempotentConsumer(simple("${body[track][start_time]}-${body[channel]}"), FileIdempotentRepository.fileIdempotentRepository(new File("repo.dat"), 250))
			.to("direct:process");
		
		from("direct:process")
			.setProperty(Exchange.CHARSET_NAME, constant("UTF-8"))
			.enrich("direct:lastfmEnricher", new LastFmAggregationStrategy())
			.marshal().json(JsonLibrary.Gson)
			.to("activemq:topic:playlists");
			
		//enrich flight with weather information
    	from("direct:lastfmEnricher")
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
    		.setHeader(Exchange.HTTP_QUERY, simple("method=track.getInfo&api_key=b25b959554ed76058ac220b7b2e0a026&artist=${header.artist}&track=${header.title}"))
			.log("headers  ${header.CamelHttpQuery}")
			.setBody().simple("1")
			.to("http://ws.audioscrobbler.com/2.0/");
	}

}
