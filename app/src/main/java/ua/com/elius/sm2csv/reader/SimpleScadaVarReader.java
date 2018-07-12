package ua.com.elius.sm2csv.reader;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SimpleScadaVarReader {

    private static final String VAR_FILE_NAME = "Variables.svr";

    private File mFile;
    private BufferedInputStream mBufferedInputStream;
    private LittleEndianDataInputStream mIn;
    private Map<String, Integer> mMap = new HashMap<>();

    public SimpleScadaVarReader(Path path) throws IOException {
        mFile = path.resolve(VAR_FILE_NAME).toFile();

        if (!mFile.exists() || !mFile.isFile()) {
            throw new IOException();
        }

        mBufferedInputStream = new BufferedInputStream(
                new FileInputStream(path.resolve(VAR_FILE_NAME).toFile()));
        mIn = new LittleEndianDataInputStream(mBufferedInputStream);
    }

    public Map<String, Integer> read() throws IOException {

        int groupsCount = readHeader();
        for (int i = 0; i < groupsCount; i++) {
            readGroup();
        }

        return mMap;
    }

    private int readHeader() throws IOException {

        skipBytesFully(8);

        return mIn.readInt();
    }

    private void readGroup() throws IOException {

        String groupName = readString();
        skipBytesFully(4);
        int varsCount = mIn.readInt();
        for (int i = 0; i < varsCount; i++) {
            readVar();
        }
        int subGroupsCount = mIn.readInt();
        for (int i = 0; i < subGroupsCount; i++) {
            readGroup();
        }
    }

    private void readVar() throws IOException {

        String varName = readString();
        int varId = mIn.readInt();
        skipBytesFully(8);
        readString(); // skip address
        skipBytesFully(22);
        readString(); // skip comment
        skipBytesFully(11);
        readString(); // skip OPC name
        readString(); // skip OPC description
        readString(); // skip OPC host name
        readString(); // skip OPC UID
        skipBytesFully(36);
        int skipNext = mIn.readInt();
        skipBytesFully(skipNext);
        skipBytesFully(18);

        mMap.put(varName, varId);
    }

    private String readString() throws IOException {

        int stringLength = mIn.readInt();
        byte[] buffer = new byte[stringLength];
        mIn.readFully(buffer);

        return new String(buffer, StandardCharsets.UTF_8);
    }
    
    private void skipBytesFully(int n) throws IOException {
        int count = 0;
        while (count < n) {
            count += mIn.skipBytes(n - count);
        }
    }
}
