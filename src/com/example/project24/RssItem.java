package com.example.project24;

public class RssItem {

    public String Title;
    public String Link;
    public String Description;
    public String Pubdate;


    public RssItem() {
        Title = "";
        Link = "";
        Description = "";
        Pubdate = "";
        
    }

    public RssItem(String title, String link, String description, String pubdate) {
       this.Title = title;
       this.Link = link;
       this.Description = description;
       this.Pubdate = pubdate;
       
        
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getLink() {
        return this.Link;
    }

    public void setLink(String link) {
        this.Link = link;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }
    
    public String getPubdate(){
    	return this.Pubdate;
    }
    public void setPubdate(String pubdate){
    	this.Pubdate = pubdate;
    }

}