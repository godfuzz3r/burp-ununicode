package com.ununicode.providers;

import com.ununicode.editortab.CustomWebSocketMessageEditorTab;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedWebSocketMessageEditor;
import burp.api.montoya.ui.editor.extension.WebSocketMessageEditorProvider;

public class CustomWebSocketMessageEditor implements WebSocketMessageEditorProvider {
    MontoyaApi api;

    public CustomWebSocketMessageEditor(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public ExtensionProvidedWebSocketMessageEditor provideMessageEditor(EditorCreationContext creationContext) {
        return new CustomWebSocketMessageEditorTab(api, creationContext);
    }
}
