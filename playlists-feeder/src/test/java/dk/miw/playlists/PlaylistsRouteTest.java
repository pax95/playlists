package dk.miw.playlists;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PlaylistsRouteTest extends CamelSpringTestSupport {

	@Test
	public void testRoute() throws Exception {
		context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// replace the from with seda:foo
				replaceFromWith("direct:start");
				weaveById("dummy").after().to("mock:last");
			}
		});
		getMockEndpoint("mock:last").expectedMessageCount(ChannelList.getChannels().size());
		template.sendBody("direct:start", "foo");
		assertMockEndpointsSatisfied();
		Thread.sleep(30000);
		// we must manually start when we are done with all the advice with
	}

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}

}
