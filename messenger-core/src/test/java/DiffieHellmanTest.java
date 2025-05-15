import org.junit.Test;
import org.testng.Assert;
import ru.java.maryan.AsymmetricCryptoAlgs.impl.DiffieHellman;

import java.util.Arrays;

public class DiffieHellmanTest {
    @Test
    public void testDiffieHellman() {
        DiffieHellman objectA = new DiffieHellman();
        DiffieHellman objectB = new DiffieHellman(objectA.getP(), objectA.getG());
        objectA.createSecretKey(objectB.getPublicKey());
        objectB.createSecretKey(objectA.getPublicKey());

        System.out.println("ObjectA shared secret: " + Arrays.toString(objectA.getSharedSecretBytes()));
        System.out.println("ObjectB shared secret: " + Arrays.toString(objectB.getSharedSecretBytes()));
        System.out.println();
        Assert.assertEquals(objectA.getSharedSecretBytes(), objectB.getSharedSecretBytes());
    }
}
