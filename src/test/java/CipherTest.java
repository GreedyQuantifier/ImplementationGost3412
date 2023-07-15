import com.Gost3412;
import com.util.ByteArrayOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CipherTest {

    static Gost3412 kuznechik =
            new Gost3412("8899aabbccddeeff0011223344556677fedcba98765432100123456789abcdef");


    @Test
    public void testIterKey() {

        List<String> strings = Arrays.asList("8899aabbccddeeff0011223344556677",
                "fedcba98765432100123456789abcdef",
                "db31485315694343228d6aef8cc78c44",
                "3d4553d8e9cfec6815ebadc40a9ffd04",
                "57646468c44a5e28d3e59246f429f1ac",
                "bd079435165c6432b532e82834da581b",
                "51e640757e8745de705727265a0098b1",
                "5a7925017b9fdd3ed72a91a22286f984",
                "bb44e25378c73123a5f32f73cdb6e517",
                "72e9dd7416bcf45b755dbaa88e4a4043"
        );


        Assertions.assertIterableEquals(
                Arrays.stream(kuznechik.getIterKeys()).map(ByteArrayOperation::encodeHexString).
                        collect(Collectors.toList()),
                strings
        );
    }


    @Test
    public void testL() {
        Assertions.assertEquals(invokeFun("linear", "64a59400000000000000000000000000"),
                "d456584dd0e3e84cc3166e4b7fa2890d");
        Assertions.assertEquals(invokeFun("linear", "d456584dd0e3e84cc3166e4b7fa2890d"),
                "79d26221b87b584cd42fbc4ffea5de9a");
        Assertions.assertEquals(invokeFun("linear", "79d26221b87b584cd42fbc4ffea5de9a"),
                "0e93691a0cfc60408b7b68f66b513c13");
        // Исправленна опечатка в ГОСТ  входных данных
        Assertions.assertEquals(invokeFun("linear", "0e93691a0cfc60408b7b68f66b513c13"),
                "e6a8094fee0aa204fd97bcb0b44b8580");
    }


    @Test
    public void testR() {

        Assertions.assertEquals(invokeFun("linearStep", "00000000000000000000000000000100"),
                "94000000000000000000000000000001");
        Assertions.assertEquals(invokeFun("linearStep", "94000000000000000000000000000001"),
                "a5940000000000000000000000000000");
        // Исправлена опечатка в выходном значении
        Assertions.assertEquals(invokeFun("linearStep", "а5940000000000000000000000000000"),
                "a3f59400000000000000000000000000");
        Assertions.assertEquals(invokeFun("linearStep", "64a59400000000000000000000000000"),
                "0d64a594000000000000000000000000");
    }


    @Test
    public void testSub() {

        Assertions.assertEquals(invokeFun("substitution", "ffeeddccbbaa99881122334455667700"),
                "b66cd8887d38e8d77765aeea0c9a7efc");
        Assertions.assertEquals(invokeFun("substitution", "b66cd8887d38e8d77765aeea0c9a7efc"),
                "559d8dd7bd06cbfe7e7b262523280d39");
        Assertions.assertEquals(invokeFun("substitution", "559d8dd7bd06cbfe7e7b262523280d39"),
                "0c3322fed531e4630d80ef5c5a81c50b");
        Assertions.assertEquals(invokeFun("substitution", "0c3322fed531e4630d80ef5c5a81c50b"),
                "23ae65633f842d29c5df529c13f5acda");
    }

    @Test
    public void testEncrypt() {
        Assertions.assertEquals(invokeFun("encrypt", "1122334455667700ffeeddccbbaa9988"),
                "7f679d90bebc24305a468d42b9d4edcd");

    }

    @Test
    public void testDecrypt() {
        Assertions.assertEquals(invokeFun("decrypt", "7f679d90bebc24305a468d42b9d4edcd"),
                "1122334455667700ffeeddccbbaa9988");
    }


    private String invokeFun(String name, String param) {
        return ByteArrayOperation.encodeHexString(invokeFun(name,
                ByteArrayOperation.decodeHexString(param)));
    }

    private byte[] invokeFun(String name, byte[] bytes) {

        try {
            Method method = Gost3412.class.getDeclaredMethod(name, byte[].class);
            method.setAccessible(true);
            return (byte[]) method.invoke(kuznechik, bytes);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


}
