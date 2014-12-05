package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;

public class IPC implements Serializable {

	private static final long serialVersionUID = 1L;
	ArrayList<String> urlPaths, serverUrls;

	public IPC() {
		urlPaths = new ArrayList<String>();
		serverUrls = new ArrayList<String>();
	}

	public ArrayList<String> getUrlStrings() {
		return urlPaths;
	}

	public void setUrlStrings(ArrayList<String> urlPaths) {
		this.urlPaths = urlPaths;
	}

	public ArrayList<String> getdifferenceServer() {
		return serverUrls;
	}

	public void setdifferenceServer(ArrayList<String> serverUrls) {
		this.serverUrls = serverUrls;
	}

}
