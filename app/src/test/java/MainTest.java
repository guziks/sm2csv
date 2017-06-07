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

    private void setWorkDir(Path casePath) {
        System.setProperty("user.dir", TEST_WORK_PATH.resolve(casePath.getFileName()).toAbsolutePath().toString());
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
            String testDir = TEST_WORK_PATH.resolve(casePath.getFileName()).toAbsolutePath().toString();
            System.out.println("Testing " + testDir);
            Main.main(new String[] {"-p", testDir});

            String[] outFileNames = casePath.resolve("out").toFile().list();
            if (outFileNames != null) {
                for (String name : outFileNames) {
                    File exp = casePath
                            .resolve("out")
                            .resolve(name)
                            .toFile();
                    File act = TEST_WORK_PATH
                            .resolve(casePath.getFileName())
                            .resolve(name)
                            .toFile();
                    System.out.println("Comparing " + " " +
                            exp.getAbsolutePath() + " " +
                            act.getAbsolutePath());
                    try {
                        assertTrue(FileUtils.contentEquals(exp, act));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
