package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.SimpleScadaRecord;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class SimpleScadaTagWriter extends CSVWriter {

    public static final String FILE_NAME = "simplescada-tags.csv";
    public static final String FILE_ENCODING = "windows-1251";

    private static final String[] HEADER1 = {"", "", "", "Архив", "", "", "", "Шкала", "", "", "", "", "", "", "", "", "", "", "", "Границы", "", "", ""};
    private static final String[] HEADER2 = {"Имя группы", "Имя переменной", "Тип данных", "Тип архивации", "Тип тренда", "Зона нечувствительности", "Интервал архивации", "Имя шкалы", "Ед. измерения", "Минимум", "Максимум", "Описание", "Тип тега", "Адрес", "Имя ПК", "OPC-сервер", "Частота опроса тегов", "Формат", "Сдвиг", "ВА", "ВП", "НП", "НА"};
    private static final String[] GROUP_HEADER = {"<group>", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    private List<String> mWrittenGroupHeaders = new ArrayList<>();

    public SimpleScadaTagWriter(Path outputPath) {
        super(getCSVFormat(),
                outputPath, FILE_NAME, FILE_ENCODING,
                CSVWriter.TARGET_TAG_SIMPLESCADA, CSVWriter.TARGET_TAG_TAG);
    }

    @Override
    void onOpen() {
        write(asList(HEADER1));
        write(asList(HEADER2));
    }

    public void write(SimpleScadaRecord record) {
        if (!mWrittenGroupHeaders.contains(record.getTagGroup())) {
            List<String> groupHeader = asList(GROUP_HEADER);
            groupHeader.set(0, record.getTagGroup());
            write(groupHeader);
            mWrittenGroupHeaders.add(record.getTagGroup());
        }
        write(record.toList());
    }

    public static CSVFormat getCSVFormat() {
        return CSVFormat.EXCEL.withDelimiter(';').withQuote(null);
    }
}
