package com.ex.file.manager;

public class MyStatus  {
	
    public Double getDiff() {
		return diff;
	}
	public MyStatus setDiff(Double diff) {
		this.diff = diff; return this;
	}
	public Double getDiffSeconds() {
		return diffSeconds;
	}
	public MyStatus setDiffSeconds(Double diffSeconds) {
		this.diffSeconds = diffSeconds;return this;
	}
	public Double getDiffMinutes() {
		return diffMinutes;
	}
	public MyStatus setDiffMinutes(Double diffMinutes) {
		this.diffMinutes = diffMinutes;return this;
	}
	public Double getDiffHours() {
		return diffHours;
	}
	public MyStatus setDiffHours(Double diffHours) {
		this.diffHours = diffHours;return this;
	}
	Double diff   ;
    Double diffSeconds  ;
    Double diffMinutes  ;
    Double diffHours   ;
    boolean isError  = Boolean.FALSE;
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
}
