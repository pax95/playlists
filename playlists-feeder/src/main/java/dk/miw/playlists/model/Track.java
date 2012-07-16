package dk.miw.playlists.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Track {
	private String artist;
	private String title;
	private String time;
	private String channnel;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getChannnel() {
		return channnel;
	}
	public void setChannnel(String channnel) {
		this.channnel = channnel;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	@SuppressWarnings("unchecked")
	public void setPayload(Map<String, Object> map) {
		Map<String, Object> now = (Map<String, Object>) map.get("now");
		this.title = (String) now.get("track_title");
		this.time = (String) now.get("start_time");
		this.artist = (String) now.get("display_artist");
		Map<String, Object> info = (Map<String, Object>) map.get("info");
		this.channnel = (String) info.get("channel");
	}
}
