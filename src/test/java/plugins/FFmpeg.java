package plugins;

import java.io.*;
import java.util.ArrayList;
import java.util.StringJoiner;

public class FFmpeg implements Runnable {
    private Process proc;
    private File captureFile;
    private int deviceId;

    public FFmpeg(File captureFile, int deviceId) {
        this.proc = null;
        this.captureFile = captureFile;
        this.deviceId = deviceId;
    }

    public void run() {
        ArrayList cmd = new ArrayList<>();
        cmd.add("ffmpeg");       // binary should be on path
        cmd.add("-y");           // always overwrite files
        cmd.add("-f");           // format
        cmd.add("avfoundation"); // apple's system audio---something else for windows
        cmd.add("-i");           // input
        cmd.add(":" + deviceId); // device id returned by ffmpeg list
        cmd.add(captureFile.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        StringJoiner out = new StringJoiner("\n");
        try {
            proc = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {

                reader.lines().forEach(out::add);
            }
            proc.waitFor();
        } catch (IOException | InterruptedException ign) {}
        System.out.println("FFMpeg output was: " + out.toString());
    }

    public void stopCollection() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        writer.write("q");
        writer.flush();
        writer.close();
    }
}
