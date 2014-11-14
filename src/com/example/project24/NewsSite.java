package com.example.project24;

public class NewsSite {
	Integer Id;
    String Title;
    String Link;
    String RssLink;
    String Description;
    String Pubdate;
    
    public 	NewsSite(){
    	Title = "";
        Link = "";
        Description = "";
        Pubdate = ""; 	
    }
    public NewsSite(String title, String link,String rsslink, String description, String pubdate){
    	this.Title = title;
    	this.Link = link;
    	this.RssLink = rsslink;
        this.Description = description;
        this.Pubdate = pubdate;
    }
    public void setId(Integer id){
    	this.Id = id;
    }
    public Integer getId(){
        return this.Id;
    }
    public void setTitle(String title){
    	this.Title = title;
    }
    public String getTitle(){
    	return this.Title;
    }
    public void setDescription(String description){
    	this.Description = description;
    }
    public String getDescription(){
    	return this.Description;
    }
    public void setLink(String link){
    	this.Link = link;
    }
    public String getLink(){
    	return this.Link;
    }
    public void setPubdate(String pubdate){
    	this.Pubdate  = pubdate;
    }
    public String getPubdate(){
    	return this.Pubdate;
    }
    public void setRssLink(String rsslink){
    	this.RssLink = rsslink;
    }
    public String getRssLink(){
    	return this.RssLink;
    }

}