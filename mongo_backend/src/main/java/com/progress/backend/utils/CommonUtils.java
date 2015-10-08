package com.progress.backend.utils;

import com.google.common.net.InternetDomainName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Armen Arzumanyan
 */
public class CommonUtils {

    private static DecimalFormatSymbols fs = new DecimalFormatSymbols();

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<String>();
        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            String url = text.substring(matchStart, matchEnd);
            urls.add(url);
        }
        return urls;
    }

    public static Double doubleValue(String strValue) {
        Double reValue = null;

        if (strValue == null || strValue.trim().equals("")) {
            return null;
        }
        fs.setGroupingSeparator(',');
        fs.setDecimalSeparator('.');
        try {
            DecimalFormat nf = new DecimalFormat("#,###,###,##0.00", fs);
            nf.setMaximumFractionDigits(3);
            nf.setMaximumIntegerDigits(3);
            reValue = nf.parse(strValue).doubleValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reValue;
    }

    public static Long longValue(String strValue) {
        Long reValue = null;
        if (strValue == null || strValue.trim().equals("")) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance();
        try {
            reValue = nf.parse(strValue).longValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reValue;
    }

    public static Integer integerValue(String strValue) {
        Integer reValue = null;
        if (strValue == null || strValue.trim().equals("")) {
            return null;
        }
        NumberFormat nf = NumberFormat.getInstance();
        try {
            reValue = nf.parse(strValue).intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reValue;
    }

    public static String getDomain(String url) {
        if (url != null) {
            url = getRedirectionUrl(url);
        }
        return url;
    }

    public static String getRedirectionUrl(String url) {
        BufferedReader in = null;
        try {
            URL urlToReturn = new URL(url);
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection uc = (HttpURLConnection) urlToReturn.openConnection();
            uc.connect();
            in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            return (uc.getHeaderField("Location"));
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }

    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            byte[] bs;
            bs = messageDigest.digest(password.getBytes());
            for (int i = 0; i < bs.length; i++) {
                String hexVal = Integer.toHexString(0xFF & bs[i]);
                if (hexVal.length() == 1) {
                    sb.append("0");
                }
                sb.append(hexVal);
            }
        } catch (NoSuchAlgorithmException ex) {
            //log.debug(ex);
        }
        return sb.toString();
    }

    public static String removeWwwFromUrl(String url) {
        int index = url.indexOf("www.");

        if (index != -1) {
            return url.substring(index + 4);
        }
        return (url);
    }

    public static String getTopPrivateDomain(String host) throws URISyntaxException {
        InternetDomainName domainName = InternetDomainName.from(host);
        return domainName.topPrivateDomain().toString();
    }

    public static void main(String args[]) throws URISyntaxException {

        System.out.println(" " + getTopPrivateDomain("news.am"));
    }
}
