package com.initech.news;

import com.initech.news.model.Rss;
import com.initech.news.util.MLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kevin on 1/10/2016.
 */
public class RssReader {

   private static final String TAG = "RssReader";

   public void read(final String httpUrl) {
      ArrayList<String> headlines = new ArrayList<>();
      ArrayList<String> links = new ArrayList<>();
      ArrayList<Rss> rssList = new ArrayList<>();
      InputStream is=null;

      try {
         final URL url = new URL(httpUrl);

         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         factory.setNamespaceAware(false);
         XmlPullParser xpp = factory.newPullParser();

         // We will get the XML from an input stream
         xpp.setInput(is=getInputStream(url), "UTF_8");

        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
         * and take in consideration only "<title>" tag which is a child of "<item>"
         *
         * In order to achieve this, we will make use of a boolean variable.
         */
         boolean insideItem = false;
         Rss rss=null;
         // Returns the type of current event: START_TAG, END_TAG, etc..
         int eventType = xpp.getEventType();
         while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

               if (xpp.getName().equalsIgnoreCase("item")) {
                  insideItem = true;
                  rss = new Rss();
               } else if (xpp.getName().equalsIgnoreCase("title")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     headlines.add(s); //extract the headline
                     MLog.i(TAG, "title: " + s);
                     rss.setTitle(s);
                  }
               } else if (xpp.getName().equalsIgnoreCase("link")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     links.add(s); //extract the link of article
                     MLog.i(TAG, "link: " + s);
                     rss.setLink(s);
                  }
               } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     MLog.i(TAG, "pubDate: " + s);
                     //todo
                  }
               } else if (xpp.getName().equalsIgnoreCase("dc:creator")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     MLog.i(TAG, "creator: " + s);
                     rss.setAuthor(s);
                  }
               } else if (xpp.getName().equalsIgnoreCase("category")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     MLog.i(TAG, "category: " + s);
                     rss.setCategory(s);
                  }
               } else if (xpp.getName().equalsIgnoreCase("description")) {
                  if (insideItem) {
                     String s = xpp.nextText();
                     MLog.i(TAG, "description: " + s);
                     rss.setDescr(s);
                  }
               }

            } else if(eventType== XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
               insideItem=false;
               rssList.add(rss);
            }

            eventType = xpp.next(); //move to next element
         }

         MLog.i(TAG,"rss list size: "+rssList.size());

      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (XmlPullParserException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (is != null) {
            try {
               is.close();
            }catch(final Exception e) {
            }
         }
      }
   }

   private InputStream getInputStream(final URL url) {
      try {
         return url.openConnection().getInputStream();
      } catch (IOException e) {
         return null;
      }
   }
}
