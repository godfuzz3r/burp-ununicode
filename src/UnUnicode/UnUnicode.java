package UnUnicode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.text.StringEscapeUtils;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorController;
import burp.IMessageEditorTab;
import burp.IRequestInfo;
import burp.ITextEditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class UnUnicode implements IMessageEditorTab{
	private ITextEditor txtInput;
	private JPanel panel = new JPanel(new BorderLayout(0, 0));

	private static IExtensionHelpers helpers;

	public byte[] concatHttp(byte[] headers, byte[] content) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(headers);
		outputStream.write(content);
		return outputStream.toByteArray();
	}


	public static boolean isJson(String content) {
		Gson gson = new Gson();
		try {
			gson.fromJson(content, Object.class);
			Object jsonObjType = gson.fromJson(content, Object.class).getClass();
			if(jsonObjType.equals(String.class)){
				return false;
			}
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}

	public String prettifyJson(String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
		JsonElement je = JsonParser.parseString(json);
		return gson.toJson(je);
	}

	public UnUnicode(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
	{
		txtInput = callbacks.createTextEditor();

		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel.add(txtInput.getComponent(), BorderLayout.CENTER);
		callbacks.customizeUiComponent(panel);
		UnUnicode.helpers = helpers;
	}

	@Override
	public String getTabCaption()
	{
		return "UnUnicode";
	}

	@Override
	public Component getUiComponent()
	{
		return panel;
	}

	@Override
	public boolean isEnabled(byte[] content, boolean isRequest)
	{
		return true;
	}

	@Override
	public void setMessage(byte[] content, boolean isRequest)
	{
		// split http headers and body
		IRequestInfo analyze = helpers.analyzeRequest(content);
		int bodyOffset = analyze.getBodyOffset();

		byte[] headers = Arrays.copyOfRange(content, 0, bodyOffset);
		byte[] body = Arrays.copyOfRange(content, bodyOffset, content.length);

		String unescaped = StringEscapeUtils.unescapeJava(new String(body));
		if (isJson(unescaped)){
			unescaped = prettifyJson(unescaped);
		}
		try{
			byte[] out = concatHttp(headers, unescaped.getBytes());
			txtInput.setText(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getMessage()
	{
		return txtInput.getSelectedText();
	}

	@Override
	public boolean isModified()
	{
		return txtInput.isTextModified();
	}

	@Override
	public byte[] getSelectedData()
	{
		return txtInput.getSelectedText();
	}
}