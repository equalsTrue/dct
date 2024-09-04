package com.dct.service.security.impl;//package com.dct.service.impl;
//
//import com.dct.model.dct.entity.AdminLogModel;
//import com.dct.model.vo.PageVO;
//import com.dct.repo.AdminLogRepo;
//import com.dct.service.IAdminLogService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Order;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author david
// * @description :
// */
//@Service
//public class AdminLogServiceImpl implements IAdminLogService {
//
//    @Autowired
//    private AdminLogRepo adminLogRepo;
//
//    @Override
//    public void save(AdminLogModel adminLogModel) {
//        adminLogRepo.save(adminLogModel);
//    }
//
//    @Override
//    public PageVO getList(int page, int limit, String userName, long begin, long end) {
//        Specification<AdminLogModel> specification = new Specification<AdminLogModel>() {
//            @Override
//            public Predicate toPredicate(Root<AdminLogModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> predicates = new ArrayList<>();
//                if(!StringUtils.isNotBlank(userName)) {
//                    predicates.add(criteriaBuilder.equal(root.get("username"), userName));
//                }
//                predicates.add(criteriaBuilder.greaterThan(root.get("createTime"), new Date(begin)));
//                predicates.add(criteriaBuilder.lessThan(root.get("createTime"), new Date(end)));
//                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
//                List<Order> orders = new ArrayList<>();
//                orders.add(criteriaBuilder.desc(root.get("createTime")));
//                orders.add(criteriaBuilder.asc(root.get("username")));
//                criteriaQuery.orderBy(orders);
//                return criteriaQuery.getRestriction();
//            }
//        };
//        PageVO<AdminLogModel> pageVO = new PageVO();
//        Long total = adminLogRepo.count(specification);
//        pageVO.setTotal(total);
//        page = page >= 1? page - 1: 0;
//        Page<AdminLogModel> models = adminLogRepo.findAll(specification, PageRequest.of(page, limit));
//        pageVO.setList(models.getContent());
//        return pageVO;
//    }
//}
