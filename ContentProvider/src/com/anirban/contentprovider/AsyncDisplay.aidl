package com.anirban.contentprovider;

// Declare the interface.
interface AsyncDisplay {
	oneway void executeDisplay(in String outputPath);
}