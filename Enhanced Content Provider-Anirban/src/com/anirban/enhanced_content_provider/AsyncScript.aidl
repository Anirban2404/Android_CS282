package com.anirban.enhanced_content_provider;

import com.anirban.enhanced_content_provider.AsyncDisplay;

// Declare the interface.
interface AsyncScript {
	oneway void downloadImage(String urlPath, AsyncDisplay callback);
}