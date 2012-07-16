package dk.miw.playlists;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class LastFmAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Map<String, Object> map = oldExchange.getIn().getBody(Map.class);
		oldExchange.setProperty("CamelCharsetName", "UTF-8");
		newExchange.setProperty("CamelCharsetName", "UTF-8");
		String body = newExchange.getIn().getBody(String.class);
		System.out.println(map + " " + body);
		// TODO Auto-generated method stub
		return oldExchange;
	}

}
