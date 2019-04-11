package com.security;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import org.zaproxy.clientapi.core.ClientApiMain;

public class ZapTools {

    private String ZAP_LOCATION = "C:\\Program Files\\OWASP\\Zed Attack Proxy";
    private String SAVE_SESSION_DIRECTORY = "ZAPSessions\\";

    public boolean startZAP() {
        try {
            String[] command = {"CMD", "/C", this.ZAP_LOCATION + "ZAP.exe"};
            ProcessBuilder proc = new ProcessBuilder(command);
            proc.directory(new File(this.ZAP_LOCATION));
            Process p = proc.start();
            p.waitFor();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            OutputStreamWriter oStream = new OutputStreamWriter(
                    p.getOutputStream());
            oStream.write("process where name='ZAP.exe'");
            oStream.flush();
            oStream.close();
            String line;
            while ((line = input.readLine()) != null) {
                //kludge to tell when ZAP is started and ready
                if (line.contains("INFO") && line.contains("org.parosproxy.paros.control.Control") && line.contains("New Session")) {
                    input.close();
                    break;
                }
            }
            System.out.println("ZAP has started successfully.");
            return true;
        } catch (Exception ex) {
            System.out.println("ZAP was unable to start.");
            ex.printStackTrace();
            return false;
        }
    }

    public void stopZAP(String zapaddr, int zapport) {
        ClientApiMain.main(new String[]{"stop", "zapaddr=" + zapaddr, "zapport=" + zapport});
    }

    public void startSession(String zapaddr, int zapport) {
        ClientApiMain.main(new String[]{"newSession", "zapaddr=" + zapaddr, "zapport=" + zapport});
        System.out.println("session started");
        System.out.println("Session started successfully.");
    }

    public void saveSession(ClientApi api, String fileName) {
        try {
            String path = this.SAVE_SESSION_DIRECTORY + fileName + ".session";
            api.core.saveSession(path, "true");
            System.out.println("Session save successful (" + path + ").");
        } catch (ClientApiException ex) {
            System.out.println("Error saving session.");
            ex.printStackTrace();
        }
    }
}