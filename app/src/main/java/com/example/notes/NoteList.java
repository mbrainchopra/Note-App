package com.example.notes;

public class NoteList {
    private String title;
    private String body;
    private String formattedDate;


    public void setTitle(String title) {

        this.title = title;
    }

    public String getTitle() {
        return title;
    }



    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
