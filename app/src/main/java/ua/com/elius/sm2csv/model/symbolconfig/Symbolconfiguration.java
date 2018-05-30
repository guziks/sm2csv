package ua.com.elius.sm2csv.model.symbolconfig;

import me.tatarka.parsnip.annotations.SerializedName;
import ua.com.elius.sm2csv.model.symbolconfig.header.Header;
import ua.com.elius.sm2csv.model.symbolconfig.node.NodeList;
import ua.com.elius.sm2csv.model.symbolconfig.type.TypeList;

public class Symbolconfiguration {

    @SerializedName("Header")
    public Header header;

    @SerializedName("TypeList")
    public TypeList typeList;

    @SerializedName("NodeList")
    public NodeList nodeList;
}
