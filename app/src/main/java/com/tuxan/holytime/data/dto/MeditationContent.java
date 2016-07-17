package com.tuxan.holytime.data.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class MeditationContent implements Parcelable {

    private String id;
    private int weekNumber;
    private String title;
    private String author;
    private String verse;
    private String body;

    public MeditationContent() {};

    public MeditationContent(Parcel source) {
        id = source.readString();
        weekNumber = source.readInt();
        title = source.readString();
        author = source.readString();
        verse = source.readString();
        body = source.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVerse() {
        return verse;
    }

    public void setVerse(String verse) {
        this.verse = verse;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static final Parcelable.Creator<MeditationContent> CREATOR = new Creator<MeditationContent>() {
        @Override
        public MeditationContent createFromParcel(Parcel source) {
            return new MeditationContent(source);
        }

        @Override
        public MeditationContent[] newArray(int size) {
            return new MeditationContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(weekNumber);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(verse);
        dest.writeString(body);
    }
}
