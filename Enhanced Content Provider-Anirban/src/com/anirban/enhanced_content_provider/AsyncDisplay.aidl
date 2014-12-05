package com.anirban.enhanced_content_provider;

// Declare the interface.
interface AsyncDisplay {
	oneway void executeDisplay(in String outputPath);
}