/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.jsf.beans.parsers;

import com.progress.backend.entities.PostBody;
import com.progress.backend.services.user.PostService;
import com.web.jsf.beans.handlers.SessionController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@ManagedBean
@ViewScoped
public class TitleExtractor implements Serializable {

    private static final long serialVersionUID = -414896421767187247L;

    private String inputUrl;
    private List<PostBody> postList = new ArrayList();
    @ManagedProperty("#{sessionController}")
    private SessionController sessionController = null;
    @ManagedProperty("#{postService}")
    private PostService postService = null;

    public void doParse() {
        try {
            String title = TitleExtractor.getPageTitle(inputUrl);
            PostBody postBody = new PostBody();
            postBody.setTitle(title);
            postBody.setUrl(inputUrl);
            Document doc = Jsoup.connect(inputUrl).get();
            Elements metalinks = doc.select("meta[property=og:image]");
            for (Element singlemeta : metalinks) {

                String image = singlemeta.attr("content");
                postBody.setImage(image);
                //System.out.println("image " + image);

            }
            metalinks = doc.select("meta[name=keywords]");

            System.out.println("keywords " + metalinks);
            if (metalinks != null) {
                String data = metalinks.attr("content");
                //System.out.println("Data Keyword " + data);
                postBody.setKeywords(data);
            }
            metalinks = doc.select("meta[name=description]");

            if (metalinks != null) {
                String data = metalinks.attr("content");
               // System.out.println(" description " + data);
                postBody.setKeywords(data);
            }
            postBody.setUserId(sessionController.getUser().getId());
            postService.save(postBody);
            //getPostList().add(postBody);
            inputUrl = null;
        } catch (IOException ex) {
            Logger.getLogger(TitleExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        try {
            String title = TitleExtractor.getPageTitle("http://news.am/eng/news/289388.html");
            //System.out.println("title " + title);
            Document doc = Jsoup.connect("http://top.rbc.ru/politics/06/10/2015/5614196e9a7947227ae0e217").get();
            //Elements metalinks = doc.select("meta"); // meta
            //  Elements metalinks = doc.select("meta[property=og:image]");
            Elements metalinks = doc.select("meta[name=keywords]");

            boolean metafound = false;
            for (Element singlemeta : metalinks) {
                System.out.println("meta " + singlemeta.toString());
                String image = singlemeta.attr("content");
                System.out.println("image " + image);

            }
        } catch (IOException ex) {
            Logger.getLogger(TitleExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* the CASE_INSENSITIVE flag accounts for
     * sites that use uppercase title tags.
     * the DOTALL flag accounts for sites that have
     * line feeds in the title text */
    private static final Pattern TITLE_TAG
            = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * @param url the HTML page
     * @return title text (null if document isn't HTML or lacks a title tag)
     * @throws IOException
     */
    public static String getPageTitle(String url) throws IOException {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();

        // ContentType is an inner class defined below
        ContentType contentType = getContentTypeHeader(conn);
        if (!contentType.contentType.equals("text/html")) {
            return null; // don't continue if not HTML
        } else {
            // determine the charset, or use the default
            Charset charset = getCharset(contentType);
            if (charset == null) {
                charset = Charset.defaultCharset();
            }

            // read the response body, using BufferedReader for performance
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            int n = 0, totalRead = 0;
            char[] buf = new char[1024];
            StringBuilder content = new StringBuilder();

            // read until EOF or first 8192 characters
            while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                content.append(buf, 0, n);
                totalRead += n;
            }
            reader.close();

            // extract the title
            Matcher matcher = TITLE_TAG.matcher(content);
            if (matcher.find()) {
                /* replace any occurrences of whitespace (which may
                 * include line feeds and other uglies) as well
                 * as HTML brackets with a space */
                return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
            } else {
                return null;
            }
        }
    }

    /**
     * Loops through response headers until Content-Type is found.
     *
     * @param conn
     * @return ContentType object representing the value of the Content-Type
     * header
     */
    private static ContentType getContentTypeHeader(URLConnection conn) {
        int i = 0;
        boolean moreHeaders = true;
        do {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName != null && headerName.equals("Content-Type")) {
                return new ContentType(headerValue);
            }

            i++;
            moreHeaders = headerName != null || headerValue != null;
        } while (moreHeaders);

        return null;
    }

    private static Charset getCharset(ContentType contentType) {
        if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName)) {
            return Charset.forName(contentType.charsetName);
        } else {
            return null;
        }
    }

    /**
     * Class holds the content type and charset (if present)
     */
    private static final class ContentType {

        private static final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        private String contentType;
        private String charsetName;

        private ContentType(String headerValue) {
            if (headerValue == null) {
                throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
            }
            int n = headerValue.indexOf(";");
            if (n != -1) {
                contentType = headerValue.substring(0, n);
                Matcher matcher = CHARSET_HEADER.matcher(headerValue);
                if (matcher.find()) {
                    charsetName = matcher.group(1);
                }
            } else {
                contentType = headerValue;
            }
        }
    }

    public List<PostBody> getPostList() {
        postList = postService.findAll();
        return postList;
    }

    public void setPostList(List<PostBody> postList) {
        this.postList = postList;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }
    
    public String getInputUrl() {
        return inputUrl;
    }

    public void setInputUrl(String inputUrl) {
        this.inputUrl = inputUrl;
    }

}
