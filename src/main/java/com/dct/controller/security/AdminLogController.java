package com.dct.controller.security;//package com.dct.controller;
//
//import com.dct.model.vo.ResponseInfoVO;
//import com.dct.utils.ResponseInfoUtil;
//import com.dct.security.JwtUtils;
//import com.dct.service.IAdminLogService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author Charles
// * @date 2019/4/28
// * @description :
// */
//@RestController
//@RequestMapping("/admin/log")
//public class AdminLogController {
//
//    @Autowired
//    private IAdminLogService adminLogService;
//
//    @Value("${jwt.header}")
//    private String tokenHeader;
//
//    @Autowired
//    JwtUtils jwtUtils;
//
//    @PostMapping(value = "/save")
//    private ResponseInfoVO log(@RequestBody AdminLogModel adminLogModel, HttpServletRequest request) {
//        String token = request.getHeader(tokenHeader);
//        String username = jwtUtils.getUsernameFromToken(token);
//        adminLogModel.setUsername(username);
//        adminLogService.save(adminLogModel);
//        return ResponseInfoUtil.success();
//    }
//
//    @GetMapping(value = "/list")
//    private ResponseInfoVO list(@RequestParam int page, @RequestParam int limit, @RequestParam(required = false) String username,
//                              @RequestParam long begin, @RequestParam long end) {
//        return ResponseInfoUtil.success(adminLogService.getList(page, limit, username, begin, end));
//    }
//}
