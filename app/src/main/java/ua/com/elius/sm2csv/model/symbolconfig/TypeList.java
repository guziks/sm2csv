package ua.com.elius.sm2csv.model.symbolconfig;

import me.tatarka.parsnip.annotations.SerializedName;

import java.util.List;

public class TypeList {

    @SerializedName("TypeSimple")
    public List<TypeSimple> typeSimple;

    @SerializedName("TypeArray")
    public List<TypeArray> typeArray;

    @SerializedName("TypeUserDef")
    public List<TypeUserDef> typeUserDef;
}
