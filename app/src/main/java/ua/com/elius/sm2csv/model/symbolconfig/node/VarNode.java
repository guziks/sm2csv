package ua.com.elius.sm2csv.model.symbolconfig.node;

import me.tatarka.parsnip.annotations.SerializedName;

public class VarNode {

    public String name;
    public String type;
    public String access;
    public String directaddress;

    @SerializedName("Comment")
    public Comment comment;
}
