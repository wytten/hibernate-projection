package org.wyttenbach.dale.document.dto;

import java.io.Serializable;
import java.util.Date;

public class DocumentDTO implements Serializable {
	private static final long serialVersionUID = -594350689811651714L;

	private Long id;

	private String busSeg;

	private String externalId;

	private String note;

	private Date receivedDate;
	
	private Long size;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusSeg() {
		return busSeg;
	}

	public void setBusSeg(String busSeg) {
		this.busSeg = busSeg;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
