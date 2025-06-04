package com.ununicode.editortab;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;

import java.awt.*;
import com.ununicode.tools.Ununicode;

public class CustomHttpRequestEditorTab implements ExtensionProvidedHttpRequestEditor {

    MontoyaApi api;
    Logging logging;
    EditorCreationContext creationContext;
    RawEditor requestEditorTab;
    HttpRequestResponse requestResponse;

    public CustomHttpRequestEditorTab(MontoyaApi api, EditorCreationContext creationContext) {
        this.api = api;
        this.creationContext = creationContext;

        this.logging = api.logging();

        requestEditorTab = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
    }

    @Override
    public HttpRequest getRequest() {
        return requestResponse.request();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        HttpRequest request = requestResponse.request();
        byte[] content = request.toByteArray().getBytes();

		int bodyOffset = request.bodyOffset();

        try {
            byte[] unsecaped = Ununicode.getUnescapedHttpContent(content, bodyOffset);
            this.requestEditorTab.setContents(ByteArray.byteArray(unsecaped));

        } catch (Exception e) {
            this.logging.logToError(e);
            this.requestEditorTab.setContents(ByteArray.byteArray(content));
        }

    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        return true;
    }

    @Override
    public String caption() {
        return "UnUnicode";
    }

    @Override
    public Component uiComponent() {
        return requestEditorTab.uiComponent();
    }

    @Override
    public Selection selectedData() {
        if(requestEditorTab.selection().isPresent()) {
            return requestEditorTab.selection().get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isModified() {
        return requestEditorTab.isModified();
    }
}
