package com.ununicode.editortab;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;

import com.ununicode.tools.Ununicode;

import java.awt.*;
public class CustomHttpResponseEditorTab implements ExtensionProvidedHttpResponseEditor {

    MontoyaApi api;
    Logging logging;
    EditorCreationContext creationContext;
    RawEditor responseEditorTab;
    HttpRequestResponse requestResponse;

    public CustomHttpResponseEditorTab(MontoyaApi api, EditorCreationContext creationContext) {
        this.api = api;
        this.creationContext = creationContext;

        this.logging = api.logging();

        if (creationContext.editorMode() == EditorMode.READ_ONLY) {
            responseEditorTab = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
        } else {
            responseEditorTab = api.userInterface().createRawEditor();
        }
    }

    @Override
    public HttpResponse getResponse() {
        return requestResponse.response();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;

        HttpResponse request = requestResponse.response();
        byte[] content = request.toByteArray().getBytes();

		int bodyOffset = request.bodyOffset();

        try {
            byte[] unsecaped = Ununicode.getUnescapedHttpContent(content, bodyOffset);
            this.responseEditorTab.setContents(ByteArray.byteArray(unsecaped));

        } catch (Exception e) {
            this.logging.logToError(e);
            this.responseEditorTab.setContents(ByteArray.byteArray(content));
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
        return responseEditorTab.uiComponent();
    }

    @Override
    public Selection selectedData() {
        if(responseEditorTab.selection().isPresent()) {
            return responseEditorTab.selection().get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isModified() {
        return responseEditorTab.isModified();
    }
}
