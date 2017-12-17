package br.ufpe.cin.if710.podcast.db.architectureComponents;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by allyson on 16/12/17.
 */
@Entity(tableName = "episodes")
public class PodcastRoom {
    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String title;
    public String pubDate;
    public String link;
    public String description;
    public String downloadLink;
    public String downloadUri;

    public PodcastRoom(){}

    public PodcastRoom(String title, String pubDate, String link, String description, String downloadLink) {
        this.title = title;
        this.pubDate = pubDate;
        this.link = link;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }
}
