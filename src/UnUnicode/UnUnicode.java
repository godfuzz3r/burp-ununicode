package UnUnicode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorController;
import burp.IMessageEditorTab;
import burp.IRequestInfo;
import burp.ITextEditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.text.translate.UnicodeUnescaper;

public class UnUnicode implements IMessageEditorTab{
	private ITextEditor txtInput;
	private JPanel panel = new JPanel(new BorderLayout(0, 0));

	private static IBurpExtenderCallbacks callbacks;
	private static IExtensionHelpers helpers;

	public byte[] concatHttp(byte[] headers, byte[] content) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(headers);
		outputStream.write(content);
		return outputStream.toByteArray();
	}

	public String prettifyJson(String json) {
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		try{
			Object jsonObject = mapper.readValue(json, Object.class);
			return mapper.writer(new BurpPrettyPrinter()).writeValueAsString(jsonObject);
 		}catch (Exception e) {
			return json;
		}
	}

	public UnUnicode(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
	{
		txtInput = callbacks.createTextEditor();

		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel.add(txtInput.getComponent(), BorderLayout.CENTER);
		callbacks.customizeUiComponent(panel);
		UnUnicode.helpers = helpers;
		UnUnicode.callbacks = callbacks;
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

		// if not json, just returns old string
		String prettified = prettifyJson(new String(body));

		// do all the magic
		String unescaped = new UnicodeUnescaper().translate(prettified);

		try{
			byte[] out = concatHttp(headers, unescaped.getBytes());
			txtInput.setText(out);
		} catch (Exception e) {
			StringWriter errorWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(errorWriter));
			callbacks.printError(errorWriter.toString());
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