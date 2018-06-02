package ua.com.elius.sm2csv.model.symbolconfig.type;

import me.tatarka.parsnip.annotations.SerializedName;
import me.tatarka.parsnip.annotations.Tag;

public class UserDefElement {

    public String type;
    public String iecname;
    public int byteoffset;

    @Tag
    @SerializedName("Comment")
    public String comment;
}
