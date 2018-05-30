package ua.com.elius.sm2csv.model.symbolconfig.node;

import me.tatarka.parsnip.annotations.SerializedName;

import java.util.List;

public class GvlNode {

    public String name;

    @SerializedName("Node")
    public List<VarNode> varNode;
}
