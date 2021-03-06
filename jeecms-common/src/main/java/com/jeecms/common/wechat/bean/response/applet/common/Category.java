package com.jeecms.common.wechat.bean.response.applet.common;

/**
 * 
 * @Description: 小程序类目实体类
 * @author: chenming
 * @date:   2018年10月30日 下午4:48:23     
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class Category {
	/** 一级类目名称*/
	private String firstClass;
	/** 二级类目名称*/
	private String secondClass;
	/** 三级类目名称*/
	private String thirdClass;
	/** 一级类目的ID编号*/
	private Integer firstId;
	/** 二级类目的ID编号*/
	private Integer secondId;
	/** 三级类目的ID编号*/
	private Integer thirdId;
	
	public String getFirstClass() {
		return firstClass;
	}
	
	public void setFirstClass(String firstClass) {
		this.firstClass = firstClass;
	}
	
	public String getSecondClass() {
		return secondClass;
	}
	
	public void setSecondClass(String secondClass) {
		this.secondClass = secondClass;
	}
	
	public String getThirdClass() {
		return thirdClass;
	}
	
	public void setThirdClass(String thirdClass) {
		this.thirdClass = thirdClass;
	}
	
	public Integer getFirstId() {
		return firstId;
	}
	
	public void setFirstId(Integer firstId) {
		this.firstId = firstId;
	}
	
	public Integer getSecondId() {
		return secondId;
	}
	
	public void setSecondId(Integer secondId) {
		this.secondId = secondId;
	}
	
	public Integer getThirdId() {
		return thirdId;
	}
	
	public void setThirdId(Integer thirdId) {
		this.thirdId = thirdId;
	}
}
