package org.idlab.cbor.encoder;

import java.io.OutputStream;

import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.MajorType;
import org.idlab.cbor.model.Tag;

public class TagEncoder extends AbstractEncoder<Tag> {

    public TagEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(Tag tag) throws CborException {
        encodeTypeAndLength(MajorType.TAG, tag.getValue());
    }

}
