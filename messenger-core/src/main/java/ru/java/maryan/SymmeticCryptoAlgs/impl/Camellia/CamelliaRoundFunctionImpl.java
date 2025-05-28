package ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia;

import ru.java.maryan.SymmeticCryptoAlgs.interfaces.Camellia.CamelliaRoundFunction;

import static ru.java.maryan.SymmeticCryptoAlgs.impl.Camellia.Utils.CamelliaUtils.xor;

public class CamelliaRoundFunctionImpl implements CamelliaRoundFunction {
    public static final byte[] S1 = {
            (byte)112, (byte)130, (byte)44, (byte)236, (byte)179, (byte)39, (byte)192, (byte)229,
            (byte)228, (byte)133, (byte)87, (byte)53, (byte)234, (byte)12, (byte)174, (byte)65,
            (byte)35, (byte)239, (byte)107, (byte)147, (byte)69, (byte)25, (byte)165, (byte)33,
            (byte)237, (byte)14, (byte)79, (byte)78, (byte)29, (byte)101, (byte)146, (byte)189,
            (byte)134, (byte)184, (byte)175, (byte)143, (byte)124, (byte)235, (byte)31, (byte)206,
            (byte)62, (byte)48, (byte)220, (byte)95, (byte)94, (byte)197, (byte)11, (byte)26,
            (byte)166, (byte)225, (byte)57, (byte)202, (byte)213, (byte)71, (byte)93, (byte)61,
            (byte)217, (byte)1, (byte)90, (byte)214, (byte)81, (byte)86, (byte)108, (byte)77,
            (byte)139, (byte)13, (byte)154, (byte)102, (byte)251, (byte)204, (byte)176, (byte)45,
            (byte)116, (byte)18, (byte)43, (byte)32, (byte)240, (byte)177, (byte)132, (byte)153,
            (byte)223, (byte)76, (byte)203, (byte)194, (byte)52, (byte)126, (byte)118, (byte)5,
            (byte)109, (byte)183, (byte)169, (byte)49, (byte)209, (byte)23, (byte)4, (byte)215,
            (byte)20, (byte)88, (byte)58, (byte)97, (byte)222, (byte)27, (byte)17, (byte)28,
            (byte)50, (byte)15, (byte)156, (byte)22, (byte)83, (byte)24, (byte)242, (byte)34,
            (byte)254, (byte)68, (byte)207, (byte)178, (byte)195, (byte)181, (byte)122, (byte)145,
            (byte)36, (byte)8, (byte)232, (byte)168, (byte)96, (byte)252, (byte)105, (byte)80,
            (byte)170, (byte)208, (byte)160, (byte)125, (byte)161, (byte)137, (byte)98, (byte)151,
            (byte)84, (byte)91, (byte)30, (byte)149, (byte)224, (byte)255, (byte)100, (byte)210,
            (byte)16, (byte)196, (byte)0, (byte)72, (byte)163, (byte)247, (byte)117, (byte)219,
            (byte)138, (byte)3, (byte)230, (byte)218, (byte)9, (byte)63, (byte)221, (byte)148,
            (byte)135, (byte)92, (byte)131, (byte)2, (byte)205, (byte)74, (byte)144, (byte)51,
            (byte)115, (byte)103, (byte)246, (byte)243, (byte)157, (byte)127, (byte)191, (byte)226,
            (byte)82, (byte)155, (byte)216, (byte)38, (byte)200, (byte)55, (byte)198, (byte)59,
            (byte)129, (byte)150, (byte)111, (byte)75, (byte)19, (byte)190, (byte)99, (byte)46,
            (byte)233, (byte)121, (byte)167, (byte)140, (byte)159, (byte)110, (byte)188, (byte)142,
            (byte)41, (byte)245, (byte)249, (byte)182, (byte)47, (byte)253, (byte)180, (byte)89,
            (byte)120, (byte)152, (byte)6, (byte)106, (byte)231, (byte)70, (byte)113, (byte)186,
            (byte)212, (byte)37, (byte)171, (byte)66, (byte)136, (byte)162, (byte)141, (byte)250,
            (byte)114, (byte)7, (byte)185, (byte)85, (byte)248, (byte)238, (byte)172, (byte)10,
            (byte)54, (byte)73, (byte)42, (byte)104, (byte)60, (byte)56, (byte)241, (byte)164,
            (byte)64, (byte)40, (byte)211, (byte)123, (byte)187, (byte)201, (byte)67, (byte)193,
            (byte)21, (byte)227, (byte)173, (byte)244, (byte)119, (byte)199, (byte)128, (byte)158
    };

    public static final byte[] S2 = {
            (byte)224, (byte)5,   (byte)88,  (byte)217, (byte)103, (byte)78,  (byte)129, (byte)203,
            (byte)201, (byte)11,  (byte)174, (byte)106, (byte)213, (byte)24,  (byte)93,  (byte)130,
            (byte)70,  (byte)223, (byte)214, (byte)39,  (byte)138, (byte)50,  (byte)75,  (byte)66,
            (byte)219, (byte)28,  (byte)158, (byte)156, (byte)58,  (byte)202, (byte)37,  (byte)123,
            (byte)13,  (byte)113, (byte)95,  (byte)31,  (byte)248, (byte)215, (byte)62,  (byte)157,
            (byte)124, (byte)96,  (byte)185, (byte)190, (byte)188, (byte)139, (byte)22,  (byte)52,
            (byte)77,  (byte)195, (byte)114, (byte)149, (byte)171, (byte)142, (byte)186, (byte)122,
            (byte)179, (byte)2,   (byte)180, (byte)173, (byte)162, (byte)172, (byte)216, (byte)154,
            (byte)23,  (byte)26,  (byte)53,  (byte)204, (byte)247, (byte)153, (byte)97,  (byte)90,
            (byte)232, (byte)36,  (byte)86,  (byte)64,  (byte)225, (byte)99,  (byte)9,   (byte)51,
            (byte)191, (byte)152, (byte)151, (byte)133, (byte)104, (byte)252, (byte)236, (byte)10,
            (byte)218, (byte)111, (byte)83,  (byte)98,  (byte)163, (byte)46,  (byte)8,   (byte)175,
            (byte)40,  (byte)176, (byte)116, (byte)194, (byte)189, (byte)54,  (byte)34,  (byte)56,
            (byte)100, (byte)30,  (byte)57,  (byte)44,  (byte)166, (byte)48,  (byte)229, (byte)68,
            (byte)253, (byte)136, (byte)159, (byte)101, (byte)135, (byte)107, (byte)244, (byte)35,
            (byte)72,  (byte)16,  (byte)209, (byte)81,  (byte)192, (byte)249, (byte)210, (byte)160,
            (byte)85,  (byte)161, (byte)65,  (byte)250, (byte)67,  (byte)19,  (byte)196, (byte)47,
            (byte)168, (byte)182, (byte)60,  (byte)43,  (byte)193, (byte)255, (byte)200, (byte)165,
            (byte)32,  (byte)137, (byte)0,   (byte)144, (byte)71,  (byte)239, (byte)234, (byte)183,
            (byte)21,  (byte)6,   (byte)205, (byte)181, (byte)18,  (byte)126, (byte)187, (byte)41,
            (byte)15,  (byte)184, (byte)7,   (byte)4,   (byte)155, (byte)148, (byte)33,  (byte)102,
            (byte)230, (byte)206, (byte)237, (byte)231, (byte)59,  (byte)254, (byte)127, (byte)197,
            (byte)164, (byte)55,  (byte)177, (byte)76,  (byte)145, (byte)110, (byte)141, (byte)118,
            (byte)3,   (byte)45,  (byte)222, (byte)150, (byte)38,  (byte)125, (byte)198, (byte)92,
            (byte)211, (byte)242, (byte)79,  (byte)25,  (byte)63,  (byte)220, (byte)121, (byte)29,
            (byte)82,  (byte)235, (byte)243, (byte)109, (byte)94,  (byte)251, (byte)105, (byte)178,
            (byte)240, (byte)49,  (byte)12,  (byte)212, (byte)207, (byte)140, (byte)226, (byte)117,
            (byte)169, (byte)74,  (byte)87,  (byte)132, (byte)17,  (byte)69,  (byte)27,  (byte)245,
            (byte)228, (byte)14,  (byte)115, (byte)170, (byte)241, (byte)221, (byte)89,  (byte)20,
            (byte)108, (byte)146, (byte)84,  (byte)208, (byte)120, (byte)112, (byte)227, (byte)73,
            (byte)128, (byte)80,  (byte)167, (byte)246, (byte)119, (byte)147, (byte)134, (byte)131,
            (byte)42,  (byte)199, (byte)91,  (byte)233, (byte)238, (byte)143, (byte)1,   (byte)61
    };

    public static final byte[] S3 = {
            (byte)56,  (byte)65,  (byte)22,  (byte)118, (byte)217, (byte)147, (byte)96,  (byte)242,
            (byte)114, (byte)194, (byte)171, (byte)154, (byte)117, (byte)6,   (byte)87,  (byte)160,
            (byte)145, (byte)247, (byte)181, (byte)201, (byte)162, (byte)140, (byte)210, (byte)144,
            (byte)246, (byte)7,   (byte)167, (byte)39,  (byte)142, (byte)178, (byte)73,  (byte)222,
            (byte)67,  (byte)92,  (byte)215, (byte)199, (byte)62,  (byte)245, (byte)143, (byte)103,
            (byte)31,  (byte)24,  (byte)110, (byte)175, (byte)47,  (byte)226, (byte)133, (byte)13,
            (byte)83,  (byte)240, (byte)156, (byte)101, (byte)234, (byte)163, (byte)174, (byte)158,
            (byte)236, (byte)128, (byte)45,  (byte)107, (byte)168, (byte)43,  (byte)54,  (byte)166,
            (byte)197, (byte)134, (byte)77,  (byte)51,  (byte)253, (byte)102, (byte)88,  (byte)150,
            (byte)58,  (byte)9,   (byte)149, (byte)16,  (byte)120, (byte)216, (byte)66,  (byte)204,
            (byte)239, (byte)38,  (byte)229, (byte)97,  (byte)26,  (byte)63,  (byte)59,  (byte)130,
            (byte)182, (byte)219, (byte)212, (byte)152, (byte)232, (byte)139, (byte)2,   (byte)235,
            (byte)10,  (byte)44,  (byte)29,  (byte)176, (byte)111, (byte)141, (byte)136, (byte)14,
            (byte)25,  (byte)135, (byte)78,  (byte)11,  (byte)169, (byte)12,  (byte)121, (byte)17,
            (byte)127, (byte)34,  (byte)231, (byte)89,  (byte)225, (byte)218, (byte)61,  (byte)200,
            (byte)18,  (byte)4,   (byte)116, (byte)84,  (byte)48,  (byte)126, (byte)180, (byte)40,
            (byte)85,  (byte)104, (byte)80,  (byte)190, (byte)208, (byte)196, (byte)49,  (byte)203,
            (byte)42,  (byte)173, (byte)15,  (byte)202, (byte)112, (byte)255, (byte)50,  (byte)105,
            (byte)8,   (byte)98,  (byte)0,   (byte)36,  (byte)209, (byte)251, (byte)186, (byte)237,
            (byte)69,  (byte)129, (byte)115, (byte)109, (byte)132, (byte)159, (byte)238, (byte)74,
            (byte)195, (byte)46,  (byte)193, (byte)1,   (byte)230, (byte)37,  (byte)72,  (byte)153,
            (byte)185, (byte)179, (byte)123, (byte)249, (byte)206, (byte)191, (byte)223, (byte)113,
            (byte)41,  (byte)205, (byte)108, (byte)19,  (byte)100, (byte)155, (byte)99,  (byte)157,
            (byte)192, (byte)75,  (byte)183, (byte)165, (byte)137, (byte)95,  (byte)177, (byte)23,
            (byte)244, (byte)188, (byte)211, (byte)70,  (byte)207, (byte)55,  (byte)94,  (byte)71,
            (byte)148, (byte)250, (byte)252, (byte)91,  (byte)151, (byte)254, (byte)90,  (byte)172,
            (byte)60,  (byte)76,  (byte)3,   (byte)53,  (byte)243, (byte)35,  (byte)184, (byte)93,
            (byte)106, (byte)146, (byte)213, (byte)33,  (byte)68,  (byte)81,  (byte)198, (byte)125,
            (byte)57,  (byte)131, (byte)220, (byte)170, (byte)124, (byte)119, (byte)86,  (byte)5,
            (byte)27,  (byte)164, (byte)21,  (byte)52,  (byte)30,  (byte)28,  (byte)248, (byte)82,
            (byte)32,  (byte)20,  (byte)233, (byte)189, (byte)221, (byte)228, (byte)161, (byte)224,
            (byte)138, (byte)241, (byte)214, (byte)122, (byte)187, (byte)227, (byte)64,  (byte)79
    };

     public static final byte[] S4 = {
            (byte)112, (byte)44,  (byte)179, (byte)192, (byte)228, (byte)87,  (byte)234, (byte)174,
            (byte)35,  (byte)107, (byte)69,  (byte)165, (byte)237, (byte)79,  (byte)29,  (byte)146,
            (byte)134, (byte)175, (byte)124, (byte)31,  (byte)62,  (byte)220, (byte)94,  (byte)11,
            (byte)166, (byte)57,  (byte)213, (byte)93,  (byte)217, (byte)90,  (byte)81,  (byte)108,
            (byte)139, (byte)154, (byte)251, (byte)176, (byte)116, (byte)43,  (byte)240, (byte)132,
            (byte)223, (byte)203, (byte)52,  (byte)118, (byte)109, (byte)169, (byte)209, (byte)4,
            (byte)20,  (byte)58,  (byte)222, (byte)17,  (byte)50,  (byte)156, (byte)83,  (byte)242,
            (byte)254, (byte)207, (byte)195, (byte)122, (byte)36,  (byte)232, (byte)96,  (byte)105,
            (byte)170, (byte)160, (byte)161, (byte)98,  (byte)84,  (byte)30,  (byte)224, (byte)100,
            (byte)16,  (byte)0,   (byte)163, (byte)117, (byte)138, (byte)230, (byte)9,   (byte)221,
            (byte)135, (byte)131, (byte)205, (byte)144, (byte)115, (byte)246, (byte)157, (byte)191,
            (byte)82,  (byte)216, (byte)200, (byte)198, (byte)129, (byte)111, (byte)19,  (byte)99,
            (byte)233, (byte)167, (byte)159, (byte)188, (byte)41,  (byte)249, (byte)47,  (byte)180,
            (byte)120, (byte)6,   (byte)231, (byte)113, (byte)212, (byte)171, (byte)136, (byte)141,
            (byte)114, (byte)185, (byte)248, (byte)172, (byte)54,  (byte)42,  (byte)60,  (byte)241,
            (byte)64,  (byte)211, (byte)187, (byte)67,  (byte)21,  (byte)173, (byte)119, (byte)128,
            (byte)130, (byte)236, (byte)39,  (byte)229, (byte)133, (byte)53,  (byte)12,  (byte)65,
            (byte)239, (byte)147, (byte)25,  (byte)33,  (byte)14,  (byte)78,  (byte)101, (byte)189,
            (byte)184, (byte)143, (byte)235, (byte)206, (byte)48,  (byte)95,  (byte)197, (byte)26,
            (byte)225, (byte)202, (byte)71,  (byte)61,  (byte)1,   (byte)214, (byte)86,  (byte)77,
            (byte)13,  (byte)102, (byte)204, (byte)45,  (byte)18,  (byte)32,  (byte)177, (byte)153,
            (byte)76,  (byte)194, (byte)126, (byte)5,   (byte)183, (byte)49,  (byte)23,  (byte)215,
            (byte)88,  (byte)97,  (byte)27,  (byte)28,  (byte)15,  (byte)22,  (byte)24,  (byte)34,
            (byte)68,  (byte)178, (byte)181, (byte)145, (byte)8,   (byte)168, (byte)252, (byte)80,
            (byte)208, (byte)125, (byte)137, (byte)151, (byte)91,  (byte)149, (byte)255, (byte)210,
            (byte)196, (byte)72,  (byte)247, (byte)219, (byte)3,   (byte)218, (byte)63,  (byte)148,
            (byte)92,  (byte)2,   (byte)74,  (byte)51,  (byte)103, (byte)243, (byte)127, (byte)226,
            (byte)155, (byte)38,  (byte)55,  (byte)59,  (byte)150, (byte)75,  (byte)190, (byte)46,
            (byte)121, (byte)140, (byte)110, (byte)142, (byte)245, (byte)182, (byte)253, (byte)89,
            (byte)152, (byte)106, (byte)70,  (byte)186, (byte)37,  (byte)66,  (byte)162, (byte)250,
            (byte)7,   (byte)85,  (byte)238, (byte)10,  (byte)73,  (byte)104, (byte)56,  (byte)164,
            (byte)40,  (byte)123, (byte)201, (byte)193, (byte)227, (byte)244, (byte)199, (byte)158
    };

         private final byte[] P_MATRIX = {
             (byte) 0, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 0, (byte) 1,
             (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 0,
             (byte) 1, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 0,
             (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1,
             (byte) 0, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 0,
             (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
             (byte) 1, (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 0, (byte) 1, (byte) 1,
             (byte) 1, (byte) 1, (byte) 1, (byte) 0, (byte) 1, (byte) 1, (byte) 0, (byte) 1,
     };

    @Override
    public byte[] performEncryptionConversion(byte[] data, byte[] roundKey) {
        return PFunc(SFunc(xor(data,roundKey)));
    }

    private byte[] PFunc(byte[] z) {
        byte[] result = new byte[8];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                if ((P_MATRIX[8*i + j] & 1) == 1) {
                    result[7 - i] ^= z[7 - j];
                }
            }
        }
        return result;
    }

    private byte[] SFunc(byte[] data) {
        byte[] output = new byte[8];

        output[0] = S1[data[0] & 0xFF];
        output[1] = S2[data[1] & 0xFF];
        output[2] = S3[data[2] & 0xFF];
        output[3] = S4[data[3] & 0xFF];
        output[4] = S2[data[4] & 0xFF];
        output[5] = S3[data[5] & 0xFF];
        output[6] = S4[data[6] & 0xFF];
        output[7] = S1[data[7] & 0xFF];

        return output;
    }
}
