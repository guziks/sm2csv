package ua.com.elius.sm2csv.model.symbolconfig;

import me.tatarka.parsnip.annotations.SerializedName;

public class Symbolconfiguration {

    @SerializedName("Header")
    public Header header;

    @SerializedName("TypeList")
    public TypeList typeList;

    @SerializedName("NodeList")
    public NodeList nodeList;
}
