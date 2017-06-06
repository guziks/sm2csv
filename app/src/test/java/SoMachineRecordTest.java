import org.junit.Test;
import ua.com.elius.sm2csv.record.SoMachineRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SoMachineRecordTest {

    @Test
    public void testFromString() {
        String[] names = {"cmd_main","primary_st","t_bk","sb_start","init_auto","sta_l_p_in","s_pp_a1"};
        String[] types = {"WORD","WORD","REAL","BOOL","BOOL","WORD","REAL"};
        String[] addresses = {"%MW4","%MW1","%MD229",null,"%MX400.0","%MW119","%MD267"};
        String[] strings = {
                "\tcmd_main AT %MW4 : WORD;",
                "\tprimary_st AT %MW1 : WORD;",
                "\tt_bk AT %MD229 :\tREAL;",
                "\tsb_start :\tBOOL;",
                "\tinit_auto    AT %MX400.0 : BOOL;",
                "\tsta_l_p_in AT %MW119: WORD;",
                "\ts_pp_a1 \tAT %MD267 : REAL;"
        };

        for (int i = 1; i < strings.length; i++) {
            SoMachineRecord.Builder expBuilder = new SoMachineRecord.Builder()
                    .name(names[i])
                    .type(types[i]);

            if (addresses[i] != null) {
                expBuilder.address(addresses[i]);
            }

            SoMachineRecord exp = expBuilder.build();
            SoMachineRecord act = SoMachineRecord.fromString(strings[i]);

            assertEquals(names[i], exp.getName(), act.getName());
            assertEquals(names[i], exp.getType(), act.getType());
            if (exp.getAddress() != null) {
                assertNotNull(names[i], act.getAddress());
                assertEquals(names[i], exp.getAddress().getType(), act.getAddress().getType());
                assertEquals(names[i], exp.getAddress().getNumber(), act.getAddress().getNumber());
                assertEquals(names[i], exp.getAddress().getDigit(), act.getAddress().getDigit());
            } else {
                assertNull(names[i], act.getAddress());
            }
        }
    }
}