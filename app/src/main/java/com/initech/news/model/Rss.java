package com.initech.news.model;

import java.text.DateFormat;

/**
 * Created by kevin on 1/10/2016.
 */
public class Rss {

   private String title, link, descr, imageUrl, videoUrl, category, author;
   private long pubDate;//Sat, 07 Sep 2002 00:00:01 GMT

   public String getLink() {
      return link;
   }

   public void setLink(String link) {
      this.link = link;
   }

   public String getDescr() {
      return descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getVideoUrl() {
      return videoUrl;
   }

   public void setVideoUrl(String videoUrl) {
      this.videoUrl = videoUrl;
   }

   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   public long getPubDate() {
      return pubDate;
   }

   public void setPubDate(long pubDate) {
      this.pubDate = pubDate;
   }

   public String getTitle() {

      return title;
   }

   public String getAuthor() {
      return author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public String toString() {
      return "RSS. title: [" + title + "] link: [" + link + "] descr: [" + descr + "] imageUrl: [" + imageUrl + "] videoUrl: [" + videoUrl + "] category: [" + category + "] pubDate: [" + pubDate + "]";
   }
}