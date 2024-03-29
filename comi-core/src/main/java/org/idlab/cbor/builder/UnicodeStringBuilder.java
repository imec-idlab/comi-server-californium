package org.idlab.cbor.builder;

import org.idlab.cbor.model.SimpleValue;

public class UnicodeStringBuilder<T extends AbstractBuilder<?>> extends
                AbstractBuilder<T> {

    public UnicodeStringBuilder(T parent) {
        super(parent);
    }

    public UnicodeStringBuilder<T> add(String string) {
        getParent().addChunk(convert(string));
        return this;
    }

    public T end() {
        getParent().addChunk(SimpleValue.BREAK);
        return getParent();
    }

}
