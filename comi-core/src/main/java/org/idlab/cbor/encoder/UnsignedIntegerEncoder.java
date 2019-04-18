package org.idlab.cbor.encoder;

import java.io.OutputStream;

import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.MajorType;
import org.idlab.cbor.model.UnsignedInteger;

public class UnsignedIntegerEncoder extends AbstractEncoder<UnsignedInteger> {

    public UnsignedIntegerEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(UnsignedInteger dataItem) throws CborException {
        encodeTypeAndLength(MajorType.UNSIGNED_INTEGER, dataItem.getValue());
    }

}
