package com.example.neelmani.fukrey.MessageHandlers;

import android.graphics.Bitmap;

public class MessageDetails {
	
	//private variables
	private String messageId;
	private String userName;
	private String agree;
	private String disagree;
	private String message;
	private String timeStamp;
	private String latitude;
	private String longitde;
	private String address;
	private String distance;
	private String lsc;
	private byte[] image;
	private Bitmap bitmapImage;

	// Empty constructor
	public MessageDetails(){
		
	}
	// constructor
	public MessageDetails(String messageId, String userName, String agree,String disagree,
						  String message,String timeStamp,  byte[]image ,String latitude, String longitude,
						  String address, String lsc, String distance){

		this.setMessageId(messageId);
		this.setUserName(userName);
		this.setAgree(agree);
		this.setDisagree(disagree);
		this.setMessage(message);
		this.setTimeStamp(timeStamp);
		this.setImage(image);
		this.setLatitude(latitude);
		this.setLongitde(longitude);
		this.setAddress(address);
		this.setDistance(distance);
		this.setLSC(lsc);
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getDisagree() {
		return disagree;
	}

	public void setDisagree(String disagree) {
		this.disagree = disagree;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String message) {
		this.timeStamp = message;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitde;
	}

	public void setLongitde(String longitde) {
		this.longitde = longitde;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLSC() {
		return lsc;
	}

	public void setLSC(String lsc) {
		this.lsc = lsc;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Bitmap getBitmapImage() {
		return bitmapImage;
	}

	public void setBitmapImage(Bitmap bitmapImage) {
		this.bitmapImage = bitmapImage;
	}
}
