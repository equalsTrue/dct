package com.dct.controller.security;

import com.alibaba.fastjson.JSONObject;
import com.dct.model.vo.AdminUserVO;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.service.security.IAdminUserService;
import com.dct.utils.ResponseInfoUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles
 */
@RestController
@RequestMapping
public class AdminUserController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	IAdminUserService adminUserService;




	/**
	 * @return
	 */
	@GetMapping(value = "/user/list")
	@ApiOperation(value = "用户列表", notes = "获取所有用户")
	public ResponseInfoVO findAll(HttpServletRequest request){
		String token = request.getHeader(tokenHeader);
		return ResponseInfoUtil.success(adminUserService.findAllUser(token));
	}

	/**
	 * @return
	 */
	@GetMapping(value = "/user/info")
	@ApiOperation(value = "获取用户自己的信息", notes = "获取用户自己的信息")
	public ResponseInfoVO findUserInfo(HttpServletRequest request){
		String token = request.getHeader(tokenHeader);
		List<AdminUserVO> adminUserVOList = new ArrayList<>();
		adminUserVOList.add(adminUserService.findUser(token));
		return ResponseInfoUtil.success(adminUserVOList);
	}

	/**
	 * @return
	 */
	@GetMapping(value = "/fetch/user")
	@ApiOperation(value = "获取用户信息", notes = "获取用户的信息")
	public ResponseInfoVO fetchUser(@RequestParam String username){
		return ResponseInfoUtil.success(adminUserService.findUserByUsername(username));
	}

	/**
	 * @param adminUserVO
	 * @return
	 */
	@PostMapping(value = "/user/save")
	@ApiOperation(value = "保存用户", notes = "保存用户")
	public ResponseInfoVO update(@ModelAttribute AdminUserVO adminUserVO){
		adminUserService.save(adminUserVO);
		return ResponseInfoUtil.success();
	}

	/**
	 * 渠道客户账号
	 * @param adminUserVO
	 * @return
	 */
	@PostMapping(value = "/user/saveChannel")
	@ApiOperation(value = "保存用户", notes = "保存用户")
	public ResponseInfoVO saveChannelUser(@ModelAttribute AdminUserVO adminUserVO){
		adminUserService.saveChannelUser(adminUserVO);
		return ResponseInfoUtil.success();
	}

	/**
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/user/get/{id}")
	@ApiOperation(value = "根据id获取用户", notes = "根据id获取用户")
	public ResponseInfoVO getById(@PathVariable String id){
		return ResponseInfoUtil.success(adminUserService.getById(id));
	}

	/**
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/user/email/get/{id}")
	@ApiOperation(value = "根据id获取用户", notes = "根据id获取用户")
	public ResponseInfoVO getEmail(@PathVariable String id){
		return ResponseInfoUtil.success(adminUserService.getById(id));
	}

	/**
	 * @param adminUserVO
	 * @return
	 */
	@PostMapping(value = "/user/password/reset")
	@ApiOperation(value = "重置密码", notes = "重置密码")
	public ResponseInfoVO resetPassword(@ModelAttribute AdminUserVO adminUserVO){
		adminUserService.resetPassword(adminUserVO);
		return ResponseInfoUtil.success();
	}

	/**
	 * @param id
	 * @param email
	 * @return
	 */
	@GetMapping(value = "/user/email/update")
	@ApiOperation(value = "修改邮箱", notes = "修改邮箱")
	public ResponseInfoVO updateEmail(@RequestParam String id, @RequestParam String email){
		adminUserService.updateEmail(id, email);
		return ResponseInfoUtil.success();
	}

	/**
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/user/delete/{id}")
	@ApiOperation(value = "删除用户", notes = "删除用户")
	public ResponseInfoVO delete(@PathVariable String id){
		adminUserService.deleteById(id);
		return ResponseInfoUtil.success();
	}

	/**
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/user/roles/{id}")
	@ApiOperation(value = "根据id获取用户角色", notes = "根据id获取用户角色")
	public ResponseInfoVO getRoleIds(@PathVariable String id){
		return ResponseInfoUtil.success(adminUserService.getRoleIds(id));
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/user/update/roles")
	@ApiOperation(value = "根据id修改用户角色", notes = "根据id修改用户角色")
	public ResponseInfoVO updateRoles(@RequestBody JSONObject jsonObject){
		adminUserService.updateRoles(jsonObject);
		return ResponseInfoUtil.success();
	}

	@GetMapping(value = "/user/fetch/checkMail")
	@ApiOperation(value = "获取用户信息", notes = "获取用户的信息")
	public ResponseInfoVO checkMail(@RequestParam String mail){
		return ResponseInfoUtil.success(adminUserService.checkMail(mail));
	}

	@GetMapping(value = "/user/get/emails")
	@ApiOperation(value = "获取用户邮件", notes = "获取用户邮件")
	public ResponseInfoVO getMails(){
		return ResponseInfoUtil.success(adminUserService.getAllEmails());
	}

}
