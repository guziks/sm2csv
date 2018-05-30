package ua.com.elius.sm2csv.model.symbolconfig.node;

import me.tatarka.parsnip.annotations.SerializedName;
import me.tatarka.parsnip.annotations.Tag;

public class VarNode {

    public String name;
    public String type;
    public String access;
    public String directaddress;

    @Tag
    @SerializedName("Comment")
    public String comment;
}
