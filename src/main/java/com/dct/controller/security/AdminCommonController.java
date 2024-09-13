package com.dct.controller.security;

import com.alibaba.fastjson.JSONObject;
import com.dct.common.constant.consist.MainConstant;
import com.dct.model.dct.AdminConfigMapModel;
import com.dct.model.vo.OptionVO;
import com.dct.model.vo.ResponseInfoVO;
import com.dct.security.ResultCode;
import com.dct.service.security.IAdminPermissionsService;
import com.dct.service.security.IAdminUserService;
import com.dct.utils.CryptoAes;
import com.dct.utils.ResponseInfoUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author ladder
 */
@RestController
@RequestMapping
public class AdminCommonController {

	@Resource
	private IAdminPermissionsService adminPermissionsService;


	@Autowired
	private IAdminUserService adminUserService;

	@Value("${jwt.header}")
	private String tokenHeader;

	private int i = 1;






	@PostMapping(value = "/admin/login")
	public ResponseInfoVO login(@RequestBody JSONObject json) throws Exception {
		String timestamp = json.getString("timestamp");
		String username = json.getString("username");
		String encryptPass = json.getString("password");
		String decryptPass = new String(CryptoAes.decrypt(encryptPass, CryptoAes.DEFAULT_KEY.getBytes("UTF-8")), "UTF-8");
		String password = decryptPass.substring(timestamp.length(), decryptPass.length());
		return ResponseInfoUtil.success(adminPermissionsService.login(username, password));
	}

	@RequestMapping(value = "/admin/logout")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header")})
	public ResponseInfoVO logout(HttpServletRequest request){
		String token = request.getHeader(tokenHeader);
		if (token == null) {
			return ResponseInfoUtil.error(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg());
		}
		adminPermissionsService.logout(token);
		return ResponseInfoUtil.ok();
	}

	/**
	 * 获取用户拥有权限的菜单
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/user/info", method = RequestMethod.GET)
	public ResponseInfoVO getUserPermittedMenuList(HttpServletRequest request) throws Exception {
		String token = request.getHeader(tokenHeader);
		String username = adminPermissionsService.getUserName(token);
		List<String> roles = adminPermissionsService.getRoleNames(token);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", username);
		jsonObj.put("roles", roles);

		JSONObject json = new JSONObject();
		json.put("routers", adminPermissionsService.getUserPermittedMenuList(token));
		json.put("info", Base64.getEncoder().encodeToString(jsonObj.toJSONString().getBytes("UTF-8")));
		return ResponseInfoUtil.success(json);
	}

	@RequestMapping(value = "/admin/user/getUserName", method = RequestMethod.GET)
	public ResponseInfoVO getUserName(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		String username = adminPermissionsService.getUserName(token);
		return ResponseInfoUtil.success(username);
	}

	/**
	 * 获取国家
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/common/countries", method = RequestMethod.GET)//todo URL 修改
	public ResponseInfoVO getCountries(HttpServletRequest request) throws Exception {
		List<AdminConfigMapModel> configMapModels = adminUserService.getConfigMap(MainConstant.COUNTRY);
		List<OptionVO> optionVOS = new ArrayList<>();
		if(configMapModels != null && configMapModels.size() > 0){//todo 构造函数
			for(AdminConfigMapModel config : configMapModels) {
				OptionVO optionVO = new OptionVO();
				optionVO.setLabel(config.getItemValue());
				optionVO.setValue(config.getItemKey());
				optionVOS.add(optionVO);
			}
		}
		return ResponseInfoUtil.success(optionVOS);
	}

}
