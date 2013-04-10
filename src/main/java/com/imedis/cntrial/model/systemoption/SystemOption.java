package com.imedis.cntrial.model.systemoption;

/**
 * 系统参数实体类
 * @author zcj
 * @create 2012-7-18 下午08:49:53
 * @version 1.0
 */
public class SystemOption {
	protected int systemOptionId;//系统参数标识
	protected String cnName;//中文名
	protected String enName;//英文标识
	protected String content;//内容
	protected int createdTimestamp;//创建时戳
	protected int creatorId;//创建人标识
	protected String creatorName;//创建人姓名
	protected int lastModified;//修改时戳
	protected int lastModifierId;//修改人标识
	protected String lastModifierName;//修改人姓名
	
	public int getSystemOptionId() {
		return systemOptionId;
	}
	public void setSystemOptionId(int systemOptionId) {
		this.systemOptionId = systemOptionId;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public int getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(int createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public int getLastModifierId() {
		return lastModifierId;
	}
	public void setLastModifierId(int lastModifierId) {
		this.lastModifierId = lastModifierId;
	}
	public String getLastModifierName() {
		return lastModifierName;
	}
	public void setLastModifierName(String lastModifierName) {
		this.lastModifierName = lastModifierName;
	}
	public int getLastModified() {
		return lastModified;
	}
	public void setLastModified(int lastModified) {
		this.lastModified = lastModified;
	}
}
