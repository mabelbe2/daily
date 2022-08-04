package plugins;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioFingerprint {

    private static String FPCALC = "src/test/java/plugins/fpcalc";

    private String fingerprint;

    AudioFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() { return fingerprint; }

    public double compare(AudioFingerprint other) {
        return FuzzySearch.partialRatio(this.getFingerprint(), other.getFingerprint());
    }

    public static AudioFingerprint calcFP(File wavFile) throws Exception {
        String output = new ProcessExecutor()
                .command(FPCALC, "-raw", wavFile.getAbsolutePath())
                .readOutput(true).execute()
                .outputUTF8();

        Pattern fpPattern = Pattern.compile("^FINGERPRINT=(.+)$", Pattern.MULTILINE);
        Matcher fpMatcher = fpPattern.matcher(output);

        String fingerprint = null;

        if (fpMatcher.find()) {
            fingerprint = fpMatcher.group(1);
        }

        if (fingerprint == null) {
            throw new Exception("Could not get fingerprint via Chromaprint fpcalc");
        }

        return new AudioFingerprint(fingerprint);
    }
}
