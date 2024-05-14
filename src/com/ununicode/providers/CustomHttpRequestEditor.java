package com.ununicode.providers;

import com.ununicode.editortab.CustomHttpRequestEditorTab;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.*;

public class CustomHttpRequestEditor implements HttpRequestEditorProvider {

    MontoyaApi api;

    public CustomHttpRequestEditor(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext) {
        return new CustomHttpRequestEditorTab(api, creationContext);
    }
}
