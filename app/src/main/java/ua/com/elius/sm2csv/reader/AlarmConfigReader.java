package ua.com.elius.sm2csv.reader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import ua.com.elius.sm2csv.model.AlarmConfigModel;
import ua.com.elius.sm2csv.writer.AlarmConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AlarmConfigReader {

    File mFile;

    public AlarmConfigReader(File file) {
        mFile = file;
    }

    public AlarmConfig read() throws FileNotFoundException, YamlException, AlarmConfigException {

        InputStreamReader streamReader =
                new InputStreamReader(new FileInputStream(mFile), StandardCharsets.UTF_8);

        YamlReader reader = new YamlReader(streamReader);
        AlarmConfigModel model = reader.read(AlarmConfigModel.class);

        // must have at least one trigger and one severity level
        if (model.numeric.trigger.size() == 0 || model.numeric.severity.size() == 0) {
            throw new AlarmConfigException();
        }

        // if have single trigger then there must be only one severity and no prefixes
        if (model.numeric.trigger.size() == 1) {
            if ((model.numeric.severity.size() != 1) || (model.numeric.prefix.size() > 0)) {
                throw new AlarmConfigException();
            }
        }

        // in case of multiple triggers lengths of lists (trigger, prefix, severity) must be equal
        if (model.numeric.trigger.size() > 1) {
            if (!(model.numeric.trigger.size() == model.numeric.prefix.size() &&
                    model.numeric.trigger.size() == model.numeric.severity.size())) {
                throw new AlarmConfigException();
            }
        }

        return new AlarmConfig(model);
    }

    public static class AlarmConfigException extends Throwable {
    }
}

