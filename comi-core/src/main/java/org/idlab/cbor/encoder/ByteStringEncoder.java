package org.idlab.cbor.encoder;

import java.io.OutputStream;

import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.ByteString;
import org.idlab.cbor.model.MajorType;
import org.idlab.cbor.model.SimpleValue;

public class ByteStringEncoder extends AbstractEncoder<ByteString> {

    public ByteStringEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(ByteString byteString) throws CborException {
        byte[] bytes = byteString.getBytes();
        if (byteString.isChunked()) {
            encodeTypeChunked(MajorType.BYTE_STRING);
            if (bytes != null) {
                encode(new ByteString(bytes));
            }
        } else if (bytes == null) {
            encoder.encode(SimpleValue.NULL);
        } else {
            encodeTypeAndLength(MajorType.BYTE_STRING, bytes.length);
            write(bytes);
        }
    }

}
