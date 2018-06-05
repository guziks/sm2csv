package ua.com.elius.sm2csv.reader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import ua.com.elius.sm2csv.model.alarmconfig.AlarmConfig;

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

        AlarmConfig config = reader.read(AlarmConfig.class);

        // TODO check config logic

        return config;
    }

    public static class AlarmConfigException extends Throwable {
    }
}

