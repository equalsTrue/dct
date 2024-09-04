package com.dct.model.dct;//package com.dct.model.dct.entity;
//
//import com.dct.model.base.EntityBase;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Table;
//
///***
// * 系统 日志
// * @author Vic.zhao
// */
//@Getter
//@Setter
//@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name = "admin_log")
//public class AdminLogModel extends EntityBase {
//
//	@Column
//	private String username;
//
//	@Column
//	private String system;
//
//	@Column
//	private String url;
//
//	@Column
//	private String method;
//
//	@Column
//	private String params;
//
//	@Column(columnDefinition="TEXT")
//	private String data;
//
//	@Column(name = "response_status")
//	private int responseStatus;
//
//	@Column(name = "response_data", columnDefinition="TEXT")
//	private String responseData;
//
//	/**
//	 * 日志内容
//	 */
//	@Column(columnDefinition="TEXT")
//	private String content;
//}
