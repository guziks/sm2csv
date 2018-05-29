package ua.com.elius.sm2csv.model.symbolconfig;

import me.tatarka.parsnip.annotations.SerializedName;

public class AppNode {

    public String name;

    @SerializedName("Node")
    public GvlNode gvlNode;
}
