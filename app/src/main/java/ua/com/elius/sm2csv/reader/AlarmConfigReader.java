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

    public AlarmConfig read() throws FileNotFoundException, YamlException {

        YamlReader reader = new YamlReader(new FileReader(mFile));
        AlarmConfigModel model = reader.read(AlarmConfigModel.class);

        return new AlarmConfig(model);
    }
}

