package ua.com.elius.sm2csv.model.symbolconfig;

import me.tatarka.parsnip.annotations.SerializedName;

import java.util.List;

public class TypeUserDef extends Type {

    public String name;
    public int size;
    public int nativesize;
    public String typeclass;
    public String iecname;

    @SerializedName("UserDefElement")
    public List<UserDefElement> userDefElement;
}
