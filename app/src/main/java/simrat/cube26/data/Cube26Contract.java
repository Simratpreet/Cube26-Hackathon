package simrat.cube26.data;

import android.provider.BaseColumns;

/**
 * Created by simrat on 12/3/16.
 */
public class Cube26Contract {
    public static abstract class PaymentGatewayEntry implements BaseColumns{
        public static final String TABLE = "payment_gateway";
        public static final String  NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String BRANDING = "branding";
        public static final String SETUP_FEE = "setup_fee";
        public static final String RATING = "rating";
        public static final String TRANS_FEE = "trans_fee";
        public static final String CURRENCIES = "currencies";
        public static final String HOW_TO_URL = "how_to_url";
        public static final String LIKES = "likes";
    }
}
