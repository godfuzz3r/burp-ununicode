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
			callbacks.printOutput(getFullExtensionName());
			callbacks.printOutput(github);
			helpers = callbacks.getHelpers();
			callbacks.setExtensionName(getFullExtensionName());
			callbacks.registerMessageEditorTabFactory(this);
	}
	
	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		return new UnUnicode(controller, false, helpers, callbacks);
	}

	private static void flushStd(){
		try{
			stdout = new PrintWriter(callbacks.getStdout(), true);
			stderr = new PrintWriter(callbacks.getStderr(), true);
		}catch (Exception e){
			stdout = new PrintWriter(System.out, true);
			stderr = new PrintWriter(System.out, true);
		}
	}

	public static PrintWriter getStdout() {
		flushStd();
		return stdout;
	}

	public static PrintWriter getStderr() {
		flushStd();
		return stderr;
	}

	//name+version+author
	public static String getFullExtensionName(){
		return ExtensionName;
	}

	public static IBurpExtenderCallbacks getCallbacks() {
		return callbacks;
	}
}