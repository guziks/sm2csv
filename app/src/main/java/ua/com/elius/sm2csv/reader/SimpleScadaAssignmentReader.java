package ua.com.elius.sm2csv.reader;

import org.apache.commons.io.FileUtils;
import ua.com.elius.sm2csv.writer.SimpleScadaAssignmentWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleScadaAssignmentReader {

    private static final Pattern VAR_PATTERN = Pattern.compile("^\\s*([^/]+)\\.Variable.+$");

    private File mInputFile;

    public SimpleScadaAssignmentReader(Path outputPath) {
        mInputFile = outputPath.resolve(SimpleScadaAssignmentWriter.FILE_NAME).toFile();
    }

    public List<String> read() throws IOException {
        List<String> uncommentedVars = new ArrayList<>();

        if (!mInputFile.exists() || !mInputFile.isFile()) {
            return null;
        }

        for (String line : FileUtils.readLines(mInputFile)) {
            Matcher matcher = VAR_PATTERN.matcher(line);
            if (matcher.matches()) {
                uncommentedVars.add(matcher.group(1));
            }
        }

        return uncommentedVars;
    }
}
