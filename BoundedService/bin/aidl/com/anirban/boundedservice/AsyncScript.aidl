package com.anirban.boundedservice;

import com.anirban.boundedservice.AsyncDisplay;

// Declare the interface.
interface AsyncScript {
	oneway void downloadScript(String urlPath,  AsyncDisplay callback);
}