package net.anvisys.xpen.Common;

import java.util.Currency;

public class APP_VARIABLES {
    public static boolean NETWORK_STATUS = false;

    public static String getCurrencySymbol(String countryCode){
        Currency currency = Currency.getInstance(countryCode);
        String symbol = currency.getSymbol();
        return symbol;
    }

}
