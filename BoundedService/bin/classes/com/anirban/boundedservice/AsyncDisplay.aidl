package com.anirban.boundedservice;

//Declare the interface.
interface AsyncDisplay {
	oneway void executeDisplay(in String outputPath);
}