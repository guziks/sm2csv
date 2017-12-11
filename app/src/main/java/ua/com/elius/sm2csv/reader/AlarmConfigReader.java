package ua.com.elius.sm2csv.reader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import ua.com.elius.sm2csv.model.AlarmConfigModel;
import ua.com.elius.sm2csv.writer.AlarmConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class AlarmConfigReader {

    File mFile;

    public AlarmConfigReader(File file) {
        mFile = file;
    }

    public AlarmConfig read() throws FileNotFoundException, YamlException, AlarmConfigException {

        YamlReader reader = new YamlReader(new FileReader(mFile));
        AlarmConfigModel model = reader.read(AlarmConfigModel.class);

        // must have at least one trigger and one severity level
        if (model.numeric.trigger.size() == 0 || model.numeric.severity.size() == 0) {
            throw new AlarmConfigException();
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
}

