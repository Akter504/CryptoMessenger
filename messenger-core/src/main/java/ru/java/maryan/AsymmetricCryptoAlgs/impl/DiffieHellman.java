package ru.java.maryan.AsymmetricCryptoAlgs.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

public class DiffieHellman {
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger sharedSecret;
    private final BigInteger p;
    private final BigInteger g;

    private static final String PRIME_HEX =
            "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
            "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
            "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
            "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
            "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
            "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
            "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
            "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B" +
            "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9" +
            "DE2BCBF6955817183995497CEA956AE515D2261898FA0510" +
            "15728E5A8AACAA68FFFFFFFFFFFFFFFF";

    public DiffieHellman() {
        this.p = new BigInteger(PRIME_HEX, 16);
        this.g = BigInteger.valueOf(2);
        generateKeys();
    }

    public DiffieHellman(byte[] p, byte[] g) {
        Objects.requireNonNull(p, "Parameter p cannot be null");
        Objects.requireNonNull(g, "Parameter g cannot be null");
        this.p = new BigInteger(1, p);
        this.g = new BigInteger(1, g);
        generateKeys();
    }

    private void generateKeys() {
        SecureRandom random = new SecureRandom();
        this.privateKey = new BigInteger(2048, random);
        this.publicKey = g.modPow(privateKey, p);
    }

    public byte[] getP() {
        return this.p.toByteArray();
    }

    public byte[] getG() {
        return this.g.toByteArray();
    }

    public byte[] getPublicKey() {
        return this.publicKey.toByteArray();
    }

    public void createSecretKey(byte[] otherPublicKeyBytes) {
        BigInteger otherPublicKey = new BigInteger(1, otherPublicKeyBytes);
        this.sharedSecret = otherPublicKey.modPow(privateKey, p);
    }

    public byte[] getSharedSecretBytes() {
        return sharedSecret.toByteArray();
    }
}
