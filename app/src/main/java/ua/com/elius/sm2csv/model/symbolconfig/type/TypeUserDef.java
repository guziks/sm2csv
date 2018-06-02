package ua.com.elius.sm2csv.model.symbolconfig.type;

import me.tatarka.parsnip.annotations.SerializedName;
import me.tatarka.parsnip.annotations.Tag;

import java.util.List;

public class TypeUserDef extends Type {

    public String name;
    public int size;
    public int nativesize;
    public String typeclass;
    public String iecname;
    public String basetype;

    @SerializedName("UserDefElement")
    public List<UserDefElement> userDefElement;

    @Tag
    @SerializedName("Comment")
    public String comment;
}
