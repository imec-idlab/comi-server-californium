package org.idlab.cbor.decoder;

import java.io.InputStream;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.Tag;

public class TagDecoder extends AbstractDecoder<Tag> {

    public TagDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public Tag decode(int initialByte) throws CborException {
        return new Tag(getLength(initialByte));
    }

}
