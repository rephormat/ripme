package com.rarchives.ripme.ripper.rippers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.rarchives.ripme.ripper.AbstractHTMLRipper;
import com.rarchives.ripme.utils.Http;

public class StufferdbRipper extends AbstractHTMLRipper {

    public StufferdbRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "stufferdb";
    }
    @Override
    public String getDomain() {
        return "stufferdb.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://stufferdb\\.com/index.php\\?/category/([0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        p = Pattern.compile("^https?://stufferdb\\.com/picture.php\\?/([0-9]+)/category/([0-9]+).*$");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        throw new MalformedURLException("Expected stufferdb.com URL format: " +
                        "stufferdb.com/index.php?/category/id - got " + url + " instead");
    }

    @Override
    public List<String> getAlbumsToQueue(Document doc) {
        List<String> urlsToAddToQueue = new ArrayList<>();
        LOGGER.info("getting albums");
        for (Element elem : doc.select("ul#thumbnails > li.gdthumb > a")) {
            urlsToAddToQueue.add(getDomain() + "/" + elem.attr("href"));
        }
        LOGGER.info(doc.html());
        return urlsToAddToQueue;
    }

    @Override
    public boolean hasQueueSupport() {
        return true;
    }

    @Override
    public boolean pageContainsAlbums(URL url) {
        Pattern p = Pattern.compile("^https?://stufferdb\\.com/index.php\\?/category/([0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        LOGGER.info("Checking if page has albums");
        LOGGER.info(m.matches());
        return m.matches();
    }

    @Override
    public Document getFirstPage() throws IOException {
        return Http.url(url).get();
    }

    @Override
    public boolean canRip(URL url) {
        Pattern p = Pattern.compile("^https?://stufferdb\\.com/index.php\\?/category/([0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return true;
        }
        p = Pattern.compile("^https?://stufferdb\\.com/picture.php\\?/([0-9]+)/category/([0-9]+).*$");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> results = new ArrayList<>();
        for (Element thumb : doc.select("video > source")) {
            String video = thumb.attr("src").replace("https", "http");
            video = video.replace("stufferdb.com.", "stufferdb.com");
            results.add(video);
        }
        return results;
    }
    @Override
    public void downloadURL(URL url, int index) {
        addURLToDownload(url, getPrefix(index));
    }
}
