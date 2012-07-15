package dk.miw.playlists;

import java.io.File;

import org.apache.camel.Exchange;
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
			.idempotentConsumer(simple("${body[track][start_time]}-${body[channel]}"), FileIdempotentRepository.fileIdempotentRepository(new File("repo.dat"), 250))
			.marshal().json(JsonLibrary.Gson)
			.to("activemq:topic:playlists");
	}

}
