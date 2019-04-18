package org.idlab.cbor.decoder;

import java.io.InputStream;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.SimpleValue;
import org.idlab.cbor.model.SimpleValueType;
import org.idlab.cbor.model.Special;
import org.idlab.cbor.model.SpecialType;

public class SpecialDecoder extends AbstractDecoder<Special> {

    private final HalfPrecisionFloatDecoder halfPrecisionFloatDecoder;
    private final SinglePrecisionFloatDecoder singlePrecisionFloatDecoder;
    private final DoublePrecisionFloatDecoder doublePrecisionFloatDecoder;

    public SpecialDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
        this.halfPrecisionFloatDecoder = new HalfPrecisionFloatDecoder(decoder, inputStream);
        this.singlePrecisionFloatDecoder = new SinglePrecisionFloatDecoder(decoder, inputStream);
        this.doublePrecisionFloatDecoder = new DoublePrecisionFloatDecoder(decoder, inputStream);
    }

    @Override
    public Special decode(int initialByte) throws CborException {
        switch (SpecialType.ofByte(initialByte)) {
        case BREAK:
            return Special.BREAK;
        case SIMPLE_VALUE:
            switch (SimpleValueType.ofByte(initialByte)) {
            case FALSE:
                return SimpleValue.FALSE;
            case TRUE:
                return SimpleValue.TRUE;
            case NULL:
                return SimpleValue.NULL;
            case UNDEFINED:
                return SimpleValue.UNDEFINED;
            case UNALLOCATED:
                return new SimpleValue(initialByte & 31);
            case RESERVED:
            default:
                throw new CborException("Not implemented");
            }
        case IEEE_754_HALF_PRECISION_FLOAT:
            return halfPrecisionFloatDecoder.decode(initialByte);
        case IEEE_754_SINGLE_PRECISION_FLOAT:
            return singlePrecisionFloatDecoder.decode(initialByte);
        case IEEE_754_DOUBLE_PRECISION_FLOAT:
            return doublePrecisionFloatDecoder.decode(initialByte);
        case SIMPLE_VALUE_NEXT_BYTE:
            return new SimpleValue(nextSymbol());
        case UNALLOCATED:
        default:
            throw new CborException("Not implemented");
        }
    }

}
