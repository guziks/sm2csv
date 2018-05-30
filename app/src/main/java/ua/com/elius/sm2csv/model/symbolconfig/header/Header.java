package ua.com.elius.sm2csv.model.symbolconfig.header;

import me.tatarka.parsnip.annotations.SerializedName;
import me.tatarka.parsnip.annotations.Tag;

public class Header {

    @Tag
    @SerializedName("Version")
    public String version;

    @SerializedName("SymbolConfigObject")
    public SymbolConfigObject symbolConfigObject;

    @SerializedName("ProjectInfo")
    public ProjectInfo projectInfo;
}
