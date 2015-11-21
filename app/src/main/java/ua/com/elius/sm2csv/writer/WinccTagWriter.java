package ua.com.elius.sm2csv.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ua.com.elius.sm2csv.record.WinccRecord;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class WinccTagWriter {
    private static final String OUTPUT_FILE_NAME = "wincc-tags.txt";
    private static final String OUTPUT_FILE_ENCODING = "UTF-16";

    private static final String[] HEADER1 = {"DmTag","Tags","Tag"};
    private static final String[] HEADER2 = {"[NAME][100][1]","[TYPE][101][1]","[LENGTH][105][1]","[FORMAT][106][1]","[CONNECTION][102][1]","[GROUP][104][1]","[ADDRPARAMS][108][1]","[SCALEVALID][112][2]","[SCALEPARAM1][113][1]","[SCALEPARAM2][114][1]","[SCALEPARAM3][115][1]","[SCALEPARAM4][116][1]","[MINLIMIT][117][1]","[MAXLIMIT][118][1]","[STARTVALUE][119][1]","[SUBSTVALUE][120][1]","[SUBSTVALUE_ON_MINLIMIT][121][2]","[SUBSTVALUE_ON_MAXLIMIT][122][2]","[SUBSTVALUE_AS_STARTVALUE][123][2]","[SUBSTVALUE_ON_ERROR][124][2]","[UPDATEMODE][111][2]","[SYNCHRONIZATION][125][2]","[RUNTIMEPERSISTENCE][126][2]"};
    private static final String[] HEADER3 = {"Name","Data type","Length","Format adaptation","Connection","Group","Address","Linear scaling","AS value range from","AS value range to","OS value range from","OS value range to","Low limit","High limit","Start value","Substitute value","Substitute value at low limit","Substitute value at high limit","Substitute value as start value","Substitute value on connection errors","Computer-local","Synchronization","Runtime persistence"};

    private static final String[] RECORD_TEMPLATE = {"<name>","<type>","<length>","<format>","<connection","","<address>","0","","","","","","","","","0","0","0","0","0","0","0"};
    private static final int NAME_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int LENGTH_INDEX = 2;
    private static final int FORMAT_INDEX = 3;
    private static final int CONNECTION_INDEX = 4;
    private static final int ADDRESS_INDEX = 6;

    private CSVPrinter printer;

    public void open() {
        CSVFormat format = CSVFormat.DEFAULT
                .withDelimiter('\t')
                .withQuote(null);
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(OUTPUT_FILE_NAME), OUTPUT_FILE_ENCODING
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            printer = new CSVPrinter(writer, format);
        } catch (IOException e) {
            e.printStackTrace();
        }

        write(Arrays.asList(HEADER1));
        write(Arrays.asList(HEADER2));
        write(Arrays.asList(HEADER3));
    }

    public void write(WinccRecord record) {
        List<String> list = Arrays.asList(RECORD_TEMPLATE);
        list.set(NAME_INDEX, record.getName());
        list.set(TYPE_INDEX, record.getType());
        list.set(LENGTH_INDEX, record.getLength());
        list.set(FORMAT_INDEX, record.getFormat());
        list.set(CONNECTION_INDEX, record.getConnection());
        list.set(ADDRESS_INDEX, record.getAddress());
        write(list);
    }

    private void write(List<String> record) {
        try {
            printer.printRecord(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
