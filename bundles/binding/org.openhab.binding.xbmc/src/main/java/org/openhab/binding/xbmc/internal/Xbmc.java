package org.openhab.binding.xbmc.internal;

import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openhab.io.net.http.HttpUtil;
import org.apache.commons.io.IOUtils;

/**
 * @author Andrea Giacosi
 * @since 1.3.0
 */
public class Xbmc {
	public Xbmc(String host, int port) {
		url = "http://" + host + ":" + port + "/jsonrpc";
		gui = new GUI();
		system = new System();
	}

	public GUI gui() {
		return gui;
	}
	
	public System system() {
		return system;
	}
	
	public class GUI {
		public void showNotification(String title, String message, int msec) {
			SortedMap<String, String> params = new TreeMap<String, String>();
			params.put("title", title);
			params.put("message", message);
			//params.put("msec???verificare", new Integer(msec).toString());
			execute(RADIX + "ShowNotification", params);
		}
		
		private static final String RADIX = "GUI.";
	}
	
	public class System {
		public void shutdown() {
			execute(RADIX + "Shutdown", null);
		}
		
		public void reboot() {
			execute(RADIX + "Reboot", null);
		}
		
		private static final String RADIX = "System.";
	}
	
	public class Application {
		public int setVolume(int volume) {
			SortedMap<String, String> params = new TreeMap<String, String>();
			params.put("volume", ((Integer)volume).toString());

			execute(RADIX + "SetVolume", params);
			return volume;
		}
		private static final String RADIX = "Application.";
	}
	
	protected Map<String, String> execute(String method, SortedMap<String, String> params) {
		StringBuilder builder = new StringBuilder();
		builder.append("\"params\":{");
		boolean isFirst = true;
		for(Entry<String,String> param:params.entrySet()) {
			if(!isFirst) {
				builder.append(",");
			}
			builder.append("\"");
			builder.append(param.getKey());
			builder.append("\":\"");
			builder.append(param.getValue());
			builder.append("\"");
			isFirst = false;
		}
		builder.append("}");
		
		String content = "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"" + method + 
				"\","+  builder.toString() + "}";
        String response = HttpUtil.executeUrl("POST", url, IOUtils.toInputStream(content), CONTENT_TYPE_JSON, 1000); 
		return null;
	}
	
	private GUI gui;
	private System system;
	private String url; 
	
	/** Constant which represents the content type <code>application/json</code> */
    private final static String CONTENT_TYPE_JSON = "application/json";
}
