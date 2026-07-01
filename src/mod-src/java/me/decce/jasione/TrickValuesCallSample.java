package me.decce.jasione;

import me.decce.transformingbase.core.Jasione;

public class TrickValuesCallSample {
    public static void init() {
        // Our transformer will identify such calls as on Enum#values, because they share the same features
        // This test makes sure our transformer can handle this correctly.
        var value1 = PublicFakeEnum.values();
        var value2 = PublicFakeEnum.values();
        if (value1 == value2 || value1.length == value2.length) {
            Jasione.LOGGER.error("Trick values call test (public) didn't pass, Jasione is not working properly");
        }
        try {
            var value3 = PrivateFakeEnum.values();
            var value4 = PrivateFakeEnum.values();
            if (value3 == value4 || value3.length == value4.length) {
                Jasione.LOGGER.error("Trick values call test (private) didn't pass, Jasione is not working properly");
            }
        }
        catch (IllegalAccessError illegalAccessError) {
            // Our transformer should handle private methods properly and use reflection
            Jasione.LOGGER.error("Trick values call test (private) didn't pass, Jasione is not working properly", illegalAccessError);
        }
    }

    private static class PublicFakeEnum {
        private static int counter;
        private static PublicFakeEnum[] values() {
            return new PublicFakeEnum[counter++];
        }
    }

    private static class PrivateFakeEnum {
        private static int counter;
        private static PrivateFakeEnum[] values() {
            return new PrivateFakeEnum[counter++];
        }
    }
}
