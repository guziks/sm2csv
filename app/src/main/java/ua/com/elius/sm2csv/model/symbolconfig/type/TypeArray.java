package ua.com.elius.sm2csv.model.symbolconfig.type;

import me.tatarka.parsnip.annotations.SerializedName;

public class TypeArray extends Type{

    public String name;
    public int size;
    public int nativesize;
    public String typeclass;
    public String iecname;
    public String basetype;

    @SerializedName("ArrayDim")
    public ArrayDim arrayDim;
}
