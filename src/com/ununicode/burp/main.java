package com.ununicode.burp;

import com.ununicode.providers.*;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

public class main implements BurpExtension {

    MontoyaApi api;
    Logging logging;

    @Override
    public void initialize(MontoyaApi api) {

        // Save a reference to the MontoyaApi object
        this.api = api;

        // api.logging() returns an object that we can use to print messages to stdout and stderr
        this.logging = api.logging();

        // Set the name of the extension
        api.extension().setName("UnUnicode");

        // Print a message to the stdout
        this.logging.logToOutput("[*] burp-ununicode loaded");
        this.logging.logToOutput("[*] github: https://github.com/godfuzz3r/burp-ununicode");

        CustomHttpRequestEditor customHttpRequestEditor = new CustomHttpRequestEditor(api);
        CustomHttpResponseEditor customHttpResponseEditor = new CustomHttpResponseEditor(api);
        CustomWebSocketMessageEditor customWebSocketMessageEditor = new CustomWebSocketMessageEditor(api);

        api.userInterface().registerHttpRequestEditorProvider(customHttpRequestEditor);
        api.userInterface().registerHttpResponseEditorProvider(customHttpResponseEditor);
        api.userInterface().registerWebSocketMessageEditorProvider(customWebSocketMessageEditor);

    }
}