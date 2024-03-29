package org.idlab.cbor.decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.ByteString;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.MajorType;
import org.idlab.cbor.model.Special;

public class ByteStringDecoder extends AbstractDecoder<ByteString> {

    public ByteStringDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public ByteString decode(int initialByte) throws CborException {
        long length = getLength(initialByte);
        if (length == INFINITY) {
            if (decoder.isAutoDecodeInfinitiveByteStrings()) {
                return decodeInfinitiveLength();
            } else {
                ByteString byteString = new ByteString(null);
                byteString.setChunked(true);
                return byteString;
            }
        } else {
            return decodeFixedLength(length);
        }
    }

    private ByteString decodeInfinitiveLength() throws CborException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataItem dataItem;
        for (;;) {
            dataItem = decoder.decodeNext();
            MajorType majorType = dataItem.getMajorType();
            if (Special.BREAK.equals(dataItem)) {
                break;
            } else if (majorType == MajorType.BYTE_STRING) {
                ByteString byteString = (ByteString) dataItem;
                byte[] byteArray = byteString.getBytes();
                if (byteArray != null) {
                    try {
                        bytes.write(byteArray);
                    } catch (IOException ioException) {
                        throw new CborException(ioException);
                    }
                }
            } else {
                throw new CborException("Unexpected major type "
                    + majorType);
            }
        }
        return new ByteString(bytes.toByteArray());
    }

    private ByteString decodeFixedLength(long length) throws CborException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream((int) length);
        for (long i = 0; i < length; i++) {
            bytes.write(nextSymbol());
        }
        return new ByteString(bytes.toByteArray());
    }

}
