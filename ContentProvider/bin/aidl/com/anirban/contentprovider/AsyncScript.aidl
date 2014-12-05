package com.anirban.contentprovider;

import com.anirban.contentprovider.AsyncDisplay;

// Declare the interface.
interface AsyncScript {
oneway void downloadImage(String urlPath, AsyncDisplay callback);
}