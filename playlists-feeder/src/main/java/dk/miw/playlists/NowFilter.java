package dk.miw.playlists;

import java.util.HashMap;
import java.util.Map;

public class NowFilter {

	public static Object filterNow(Map<String, Object> map) {
		Object now = map.get("now");
		@SuppressWarnings("unchecked")
		Map<String, Object> info = (Map<String, Object>) map.get("info");
		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("track", now);
		answer.put("channel", info.get("channel"));
		return answer;
	}

}
