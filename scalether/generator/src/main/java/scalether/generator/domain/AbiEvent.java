package scalether.generator.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import scalether.generator.util.Hex;
import scalether.util.Hash;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AbiEvent implements AbiItem {
    private String name;
    private List<AbiEventParam> inputs = Collections.emptyList();
    private boolean anonymous = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbiEventParam> getInputs() {
        return inputs;
    }

    public void setInputs(List<AbiEventParam> inputs) {
        this.inputs = inputs;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getType() {
        return "event";
    }

    public String getId() {
        return Hex.prefixed(Hash.sha3(toString().getBytes(StandardCharsets.US_ASCII)));
    }

    @JsonIgnore
    public List<AbiEventParam> getIndexed() {
        return inputs.stream().filter(AbiEventParam::isIndexed).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<AbiEventParam> getNonIndexed() {
        return inputs.stream().filter(e -> !e.isIndexed()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<AbiEventParam> getAll() {
        ArrayList<AbiEventParam> result = new ArrayList<>();
        result.addAll(getIndexed());
        result.addAll(getNonIndexed());
        return result;
    }

    @Override
    public String toString() {
        return name + "(" + inputs.stream().map(AbiEventParam::getType).collect(Collectors.joining(",")) + ")";
    }
}
