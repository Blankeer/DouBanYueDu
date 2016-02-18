package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

class HashMacro extends FunctionCallImplementation {
    private static final String ALGORITHM;
    private static final String ARG0;
    private static final String DEFAULT_ALGORITHM = "MD5";
    private static final String DEFAULT_INPUT_FORMAT = "text";
    private static final String ID;
    private static final String INPUT_FORMAT;

    static {
        ID = FunctionType.HASH.toString();
        ARG0 = Key.ARG0.toString();
        ALGORITHM = Key.ALGORITHM.toString();
        INPUT_FORMAT = Key.INPUT_FORMAT.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public HashMacro() {
        super(ID, ARG0);
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> parameters) {
        Value argumentParameter = (Value) parameters.get(ARG0);
        if (argumentParameter == null || argumentParameter == Types.getDefaultValue()) {
            return Types.getDefaultValue();
        }
        byte[] bytesToHash;
        String argument = Types.valueToString(argumentParameter);
        Value algorithmParameter = (Value) parameters.get(ALGORITHM);
        String algorithm = algorithmParameter == null ? DEFAULT_ALGORITHM : Types.valueToString(algorithmParameter);
        Value inputFormatParameter = (Value) parameters.get(INPUT_FORMAT);
        String inputFormat = inputFormatParameter == null ? DEFAULT_INPUT_FORMAT : Types.valueToString(inputFormatParameter);
        if (DEFAULT_INPUT_FORMAT.equals(inputFormat)) {
            bytesToHash = argument.getBytes();
        } else if ("base16".equals(inputFormat)) {
            bytesToHash = Base16.decode(argument);
        } else {
            Log.e("Hash: unknown input format: " + inputFormat);
            return Types.getDefaultValue();
        }
        try {
            return Types.objectToValue(Base16.encode(hash(algorithm, bytesToHash)));
        } catch (NoSuchAlgorithmException e) {
            Log.e("Hash: unknown algorithm: " + algorithm);
            return Types.getDefaultValue();
        }
    }

    private byte[] hash(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(bytes);
        return digest.digest();
    }
}
