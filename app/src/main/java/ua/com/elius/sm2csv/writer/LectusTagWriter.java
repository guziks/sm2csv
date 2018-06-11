package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import ua.com.elius.sm2csv.record.LectusRecord;

import java.nio.file.Path;

import static java.util.Arrays.asList;

public class LectusTagWriter extends CSVWriter {

    private static final String OUTPUT_FILE_NAME = "lectus-tags.csv";
    private static final String OUTPUT_FILE_ENCODING = "UTF-16LE";

    private static final String[][] HEADERS = {
            {"OPC_SERVER_CONFIG"},
            {""},
            {"PROPERTIES"},
            {"Plugin","PropertyID","DataType","Description"},
            {""},
            {"Modbus","1","2","Тип переменной"},
            {"Modbus","2","12","Значение переменной"},
            {"Modbus","3","2","Качество переменной"},
            {"Modbus","4","7","Метка времени"},
            {"Modbus","5","3","Права доступа"},
            {"Modbus","7","3","Тип(значение/перечисление)"},
            {"Modbus","101","8","Описание узла"},
            {"Modbus","5003","18","Адрес переменной"},
            {"Modbus","5041","17","Номер бита"},
            {"Modbus","5006","18","Значимые биты данных"},
            {""},
            {"ITEMS"},
            {"OwnerItemID","Name","Plugin","Flag","AccessRights","AccessChange","DataType","EUType","EUInfo","PropertyID","PropertyValue","PropertyID","PropertyValue","..."},
            {""}
    };

    public LectusTagWriter(Path outputPath) {
        super(CSVFormat.EXCEL.withDelimiter(';').withQuote(null),
                outputPath, OUTPUT_FILE_NAME, OUTPUT_FILE_ENCODING, false,
                CSVWriter.TARGET_TAG_LECTUS, CSVWriter.TARGET_TAG_TAG);
    }

    @Override
    void onOpen() {
        for (String[] header : HEADERS) {
            write(asList(header));
        }
    }

    public void write(LectusRecord record) {
        write(record.toList());
    }
}
