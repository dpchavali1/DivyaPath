package com.divyapath.app.utils;
import android.net.Uri;
public class AffiliateUrlBuilder {
    private static final String AMAZON_BASE="https://www.amazon.in/dp/";
    private static final String AMAZON_TAG="syncflowin-21";
    public static String buildAmazonUrl(String asin){return Uri.parse(AMAZON_BASE+asin).buildUpon().appendQueryParameter("tag",AMAZON_TAG).appendQueryParameter("utm_source","divyapath").build().toString();}
    public static String buildMakeMyTripUrl(String city){return Uri.parse("https://www.makemytrip.com/hotels/").buildUpon().appendQueryParameter("city",city).appendQueryParameter("utm_source","divyapath").build().toString();}
}
