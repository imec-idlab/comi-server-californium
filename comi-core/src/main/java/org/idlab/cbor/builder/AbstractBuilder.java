package org.idlab.cbor.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.idlab.cbor.CborException;
import org.idlab.cbor.decoder.HalfPrecisionFloatDecoder;
import org.idlab.cbor.encoder.HalfPrecisionFloatEncoder;
import org.idlab.cbor.model.ByteString;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.DoublePrecisionFloat;
import org.idlab.cbor.model.HalfPrecisionFloat;
import org.idlab.cbor.model.NegativeInteger;
import org.idlab.cbor.model.SimpleValue;
import org.idlab.cbor.model.SinglePrecisionFloat;
import org.idlab.cbor.model.Tag;
import org.idlab.cbor.model.UnicodeString;
import org.idlab.cbor.model.UnsignedInteger;

public abstract class AbstractBuilder<T> {

    private final T parent;

    public AbstractBuilder(T parent) {
        this.parent = parent;
    }

    protected T getParent() {
        return parent;
    }

    protected void addChunk(DataItem dataItem) {
        throw new IllegalStateException();
    }

    protected DataItem convert(long value) {
        if (value >= 0) {
            return new UnsignedInteger(value);
        } else {
            return new NegativeInteger(value);
        }
    }

    protected DataItem convert(BigInteger value) {
        if (value.signum() == -1) {
            return new NegativeInteger(value);
        } else {
            return new UnsignedInteger(value);
        }
    }

    protected DataItem convert(boolean value) {
        if (value) {
            return SimpleValue.TRUE;
        } else {
            return SimpleValue.FALSE;
        }
    }

    protected DataItem convert(byte[] bytes) {
        return new ByteString(bytes);
    }

    protected DataItem convert(String string) {
        return new UnicodeString(string);
    }

    protected DataItem convert(float value) {
        if (isHalfPrecisionEnough(value)) {
            return new HalfPrecisionFloat(value);
        } else {
            return new SinglePrecisionFloat(value);
        }
    }

    protected DataItem convert(double value) {
        return new DoublePrecisionFloat(value);
    }

    protected Tag tag(long value) {
        return new Tag(value);
    }

    private boolean isHalfPrecisionEnough(float value) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HalfPrecisionFloatEncoder encoder = getHalfPrecisionFloatEncoder(outputStream);
            encoder.encode(new HalfPrecisionFloat(value));
            byte[] bytes = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            HalfPrecisionFloatDecoder decoder = getHalfPrecisionFloatDecoder(inputStream);
            if (inputStream.read() == -1) { // to skip type byte
                throw new CborException("unexpected end of stream");
            }
            HalfPrecisionFloat halfPrecisionFloat = decoder.decode(0);
            return value == halfPrecisionFloat.getValue();
        } catch (CborException cborException) {
            return false;
        }
    }

    protected HalfPrecisionFloatEncoder getHalfPrecisionFloatEncoder(OutputStream outputStream) {
        return new HalfPrecisionFloatEncoder(null, outputStream);
    }

    protected HalfPrecisionFloatDecoder getHalfPrecisionFloatDecoder(InputStream inputStream) {
        return new HalfPrecisionFloatDecoder(null, inputStream);
    }

}
