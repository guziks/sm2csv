import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.elius.sm2csv.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MainTest {

    private static final Path TEST_PATH = Paths.get("test");
    private static final Path SAMPLES_PATH = TEST_PATH.resolve("samples");
    private static final Path TEST_WORK_PATH = TEST_PATH.resolve("work");

    private static Stream<Path> getCasesPaths () {
        Stream<Path> paths = null;
        try {
            paths = Files.list(SAMPLES_PATH).sorted();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(paths);
        return paths;
    }

    private static void copyCase(Path casePath) {
        File src = casePath.resolve("in").toFile();
        File dst = TEST_WORK_PATH.resolve(casePath.getFileName()).toFile();
        try {
            FileUtils.copyDirectory(src, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCase(Path casePath) {
        File caseDir = TEST_WORK_PATH.resolve(casePath.getFileName()).toFile();
        try {
            FileUtils.deleteDirectory(caseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllCases() {
        try {
            FileUtils.deleteDirectory(TEST_WORK_PATH.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getCaseWorkPath(Path casePath) {
        return TEST_WORK_PATH.resolve(casePath.getFileName()).toAbsolutePath();
    }

    private static Path getCaseOutPath(Path casePath) {
        return casePath.resolve("out");
    }

    @BeforeClass
    public static void haveSamples() {
        assertTrue("Samples folder exists", Files.isDirectory(SAMPLES_PATH));

        String case1 = ((Path) getCasesPaths().toArray()[0]).getFileName().toString();
        assertEquals("case1", case1);
    }

    @AfterClass
    public static void clean() {
        deleteAllCases();
    }

    @Test
    public void testOnSamples() {
        getCasesPaths().forEach(casePath -> {
            copyCase(casePath);

            Main.main(new String[] {"-p", getCaseWorkPath(casePath).toString()});

            String[] outFileNames = getCaseOutPath(casePath).toFile().list();
            if (outFileNames != null) {
                for (String name : outFileNames) {
                    File exp = getCaseOutPath(casePath).resolve(name).toFile();
                    File act = getCaseWorkPath(casePath).resolve(name).toFile();
                    System.out.println("Comparing\n" + exp.getAbsolutePath() + "\n" + act.getAbsolutePath());
                    try {
                        assertTrue(FileUtils.contentEquals(exp, act));
                        System.out.println("Equals\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
