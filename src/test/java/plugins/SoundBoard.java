package plugins;

import java.io.*;
import java.util.ArrayList;
import java.util.StringJoiner;

public class SoundBoard implements Runnable {
    private Process proc;
    private String hostInputSourceId;

    public SoundBoard(String hostInputSourceId) {
        this.proc = null;
        this.hostInputSourceId = hostInputSourceId;
    }

    public void run() {
        ArrayList cmd = new ArrayList<>();
        cmd.add("curl");
        cmd.add("-X");
        cmd.add("POST");
        cmd.add("-H");
        cmd.add("Content-Type:multipart/form-data");
        cmd.add("localhost:8080/sources/play");
        cmd.add("-F");
        String fileParam = "audioFile=@mic_sample_sound.wav";
        cmd.add(fileParam);
        cmd.add("-F");
        String lastParam = "sourceCommandRequest={\"sourceId\":" + hostInputSourceId+ "};type=application/json";
        cmd.add(lastParam);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        pb.directory(new File(System.getProperty("user.dir")+ "/src/test/audios"));
        StringJoiner out = new StringJoiner("\n");
        try {
            proc = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {

                reader.lines().forEach(out::add);
            }
            proc.waitFor();
        } catch (IOException | InterruptedException ign) {
            ign.printStackTrace();
        }
        System.out.println("SoundBoard output was: " + out.toString());
    }
}
