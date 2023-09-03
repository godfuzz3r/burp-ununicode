package burp;

import java.io.PrintWriter;

import UnUnicode.UnUnicode;

public class BurpExtender implements IBurpExtender,IMessageEditorTabFactory
{
	private static IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	
	private static PrintWriter stdout;
	private static PrintWriter stderr;
	public static String ExtensionName = "UnUnicode";
	public static String Author = "godfuzz3r";
	public String github = "https://github.com/godfuzz3r/burp-ununicode";

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
	{
			BurpExtender.callbacks = callbacks;
			callbacks.printOutput(ExtensionName);
			callbacks.printOutput(github);
			helpers = callbacks.getHelpers();
			callbacks.setExtensionName(ExtensionName);
			callbacks.registerMessageEditorTabFactory(this);
	}
	
	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		return new UnUnicode(controller, false, helpers, callbacks);
	}

	public static PrintWriter getStdout() {
		return stdout;
	}

	public static PrintWriter getStderr() {
		return stderr;
	}

	public static IBurpExtenderCallbacks getCallbacks() {
		return callbacks;
	}
}