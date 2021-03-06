package burp;

import burp.scanner.CspHeaderScanner;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender, IMessageEditorTabFactory {

    private  IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private CspHeaderScanner scanner;

    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {

        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        this.callbacks.setExtensionName("CSP Auditor");

        PrintWriter stdout = new PrintWriter(callbacks.getStdout(), true);
        stdout.println("== CSP Auditor plugin ==");
        stdout.println("This plugin provided a readable view of CSP headers in Response Tab. ");
        stdout.println("It also include Passive scan rules to detect weak CSP configuration.");
        stdout.println(" - Github : https://github.com/GoSecure/csp-auditor");
        stdout.println("");
        stdout.println("== License ==");
        stdout.println("CSP Auditor plugin is release under LGPL.");
        stdout.println("");

        Log.setLogger(new Log.Logger() {
            @Override
            protected void print(String message) {
                try {
                    callbacks.getStdout().write(message.getBytes());
                    callbacks.getStdout().write('\n');
                } catch (IOException e) {
                    System.err.println("Error while printing the log : " + e.getMessage()); //Very unlikely
                }
            }
        });
        Log.DEBUG();

        this.callbacks.registerMessageEditorTabFactory(this);

        scanner = new CspHeaderScanner(helpers);
        this.callbacks.registerScannerCheck(scanner);
    }


    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController iMessageEditorController, boolean b) {
        return new CspTab(this.callbacks, this.helpers, iMessageEditorController);
    }
}
