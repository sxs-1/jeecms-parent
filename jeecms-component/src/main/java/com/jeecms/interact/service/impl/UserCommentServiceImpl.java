/*  
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.interact.service.impl;

import com.jeecms.auth.domain.CoreUser;
import com.jeecms.auth.service.CoreUserService;
import com.jeecms.common.base.service.BaseServiceImpl;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.SystemExceptionInfo;
import com.jeecms.common.exception.error.ContentErrorCodeEnum;
import com.jeecms.common.exception.error.SettingErrorCodeEnum;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.util.MyDateUtils;
import com.jeecms.common.web.Location;
import com.jeecms.common.web.Location.LocationResult.AdInfo;
import com.jeecms.common.web.util.RequestUtils;
import com.jeecms.content.constants.ContentConstant;
import com.jeecms.content.domain.Content;
import com.jeecms.content.domain.vo.ContentFrontVo;
import com.jeecms.content.service.ContentFrontService;
import com.jeecms.content.service.ContentService;
import com.jeecms.interact.constants.UserCommentConstan;
import com.jeecms.interact.dao.UserCommentDao;
import com.jeecms.interact.domain.UserComment;
import com.jeecms.interact.domain.UserCommentReport;
import com.jeecms.interact.domain.dto.UserCommentDto;
import com.jeecms.interact.domain.dto.UserCommentTopDto;
import com.jeecms.interact.domain.vo.UserCommentCountVo;
import com.jeecms.interact.domain.vo.UserInteractionMoblieVo;
import com.jeecms.interact.domain.vo.UserInteractionVo;
import com.jeecms.interact.service.UserCommentReportService;
import com.jeecms.interact.service.UserCommentService;
import com.jeecms.interact.util.UserCommentUtil;
import com.jeecms.member.domain.MemberScoreDetails;
import com.jeecms.member.service.MemberScoreDetailsService;
import com.jeecms.publish.domain.vo.ContentLikeVo;
import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.domain.SysBlackList;
import com.jeecms.system.domain.vo.MassScoreVo;
import com.jeecms.system.domain.vo.SysBlackListCommentVo;
import com.jeecms.system.service.AddressService;
import com.jeecms.system.service.CmsSiteService;
import com.jeecms.system.service.SysBlackListService;
import com.jeecms.util.SystemContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户评论service实现类
 * 
 * @author: chenming
 * @date: 2019年5月6日 上午9:04:52
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserCommentServiceImpl extends BaseServiceImpl<UserComment, UserCommentDao, Integer>
		implements UserCommentService {

	static Logger logger = LoggerFactory.getLogger(UserCommentServiceImpl.class);
	
	@Override
	public Page<UserComment> findByList(Integer siteId, Short status, Boolean isTop, Boolean isReply, Integer channelId,
			Date startTime, Date endTime, Date replyStartTime, Date replyEndTime, String fuzzySearch, 
			String userName,String ip, String commentText, String replytText, String title, 
			Pageable pageable) throws GlobalException {
	    // 通过筛选条件查询出符合条件的评论集合
		Page<UserComment> uPage = dao.findByList(siteId, status, isTop, isReply, channelId, startTime, endTime,
				replyStartTime, replyEndTime, fuzzySearch, userName, ip, commentText, replytText, 
				title, pageable);
		// 查询到黑名单
		SysBlackListCommentVo sysBlackListCommentVo = sListService.findBySiteId(siteId,SysBlackList.USER_COMMENT_TYPE);
		// 查询出黑名单被禁止的用户id集合
		List<Integer> userIdList = sysBlackListCommentVo.getUserIds();
		// 查询出黑名单被禁止的IP集合
		List<String> ipList = sysBlackListCommentVo.getIps();
		List<Integer> reportIds = commentReportService.findByDeleted();
		reportIds = reportIds == null ? new ArrayList<>() : reportIds;
		for (UserComment userComment : uPage) {
		    UserCommentUtil.checkParameter(userComment, replytText, userIdList, ipList);
			// 是否被举报
			if (reportIds.contains(userComment.getId())) {
				userComment.setIsReport(true);
			}
			UserCommentCountVo vo = this.getCount(userComment.getContentId(), null);
			userComment.setAllNum(vo.getAllNum());
			userComment.setPendingReview(vo.getPendingReviewNum());
			userComment.setSuccessReview(vo.getSuccessReviewNum());
			userComment.setErrorReview(vo.getErrorReviewNum());
		}
		return uPage;
	}


	@Override
	public Page<UserComment> findByList(Integer siteId, Short status, Boolean isTop, Boolean isReply, Integer channelId,
			Date startTime, Date endTime, Date replyStartTime, Date replyEndTime, String fuzzySearch, 
			String userName, String ip, String commentText, String replytText, String title, 
			Integer contentId, Integer userId, String precisionIp, Pageable pageable) 
					throws GlobalException {
		Page<UserComment> uPage = dao.findTermByList(siteId, status, isTop, isReply, channelId, startTime, endTime,
				replyStartTime, replyEndTime, fuzzySearch, userName, ip, commentText, replytText, title,
				contentId, userId, precisionIp, pageable);
        // 查询到黑名单
        SysBlackListCommentVo sysBlackListCommentVo = sListService.findBySiteId(siteId,SysBlackList.USER_COMMENT_TYPE);
        // 查询出黑名单被禁止的用户id集合
        List<Integer> userIdList = sysBlackListCommentVo.getUserIds();
        // 查询出黑名单被禁止的IP集合
        List<String> ipList = sysBlackListCommentVo.getIps();
		for (UserComment userComment : uPage) {
            UserCommentUtil.checkParameter(userComment, replytText, userIdList, ipList);
		}
		return uPage;
	} 
	
	@Override
	public Page<UserComment> findByList(Integer siteId,Pageable pageable) throws GlobalException {
		Page<UserComment> uPage = dao.findReportByList(siteId, pageable);
        // 查询到黑名单
        SysBlackListCommentVo sysBlackListCommentVo = sListService.findBySiteId(siteId,SysBlackList.USER_COMMENT_TYPE);
        // 查询出黑名单被禁止的用户id集合
        List<Integer> userIdList = sysBlackListCommentVo.getUserIds();
        // 查询出黑名单被禁止的IP集合
        List<String> ipList = sysBlackListCommentVo.getIps();
		for (UserComment userComment : uPage) {
			UserCommentUtil.checkParameter(userComment, null, userIdList, ipList);
			List<UserCommentReport> userCommentReports = userComment.getUserCommentReports();
			if (!CollectionUtils.isEmpty(userCommentReports)) {
				userCommentReports = userCommentReports.stream()
						.sorted(Comparator.comparing(UserCommentReport::getCreateTime).reversed())
						.collect(Collectors.toList());
				userComment.setUserCommentReports(userCommentReports);
			}
		}
		return uPage;
	}

	@Override
	public void saveOrUpdateReply(UserComment userComment, UserCommentDto dto,HttpServletRequest request,
			CmsSite site) throws GlobalException {
		if (dto.getIsUpdate()) {
			if (userComment.getReplyAdminCommentId() == null) {
				return;
			}
			UserComment reply = super.findById(userComment.getReplyAdminCommentId());
			reply.setCommentText(dto.getReplytText());
			userComment.setReplyTime(new Date());
			super.updateAll(reply);
		} else {
			if (userComment.getReplyAdminCommentId() != null) {
				return;
			}
			CoreUser user = SystemContextUtils.getCoreUser();
			Integer contentId = userComment.getContentId();
			// 新增一个回复数据
			UserComment newComment = new UserComment();
			Content content = contentService.findById(contentId);
			newComment.setSiteId(userComment.getSiteId());
			newComment.setUserId(user.getId());
			newComment.setContentId(contentId);
			newComment.setChannelId(content.getChannelId());
			newComment.setCommentText(dto.getReplytText());
			String ip = RequestUtils.getRemoteAddr(request);
			newComment.setIp(ip);
			newComment.setVisitorArea(this.getVersionArea(ip));
			newComment.setStatus(UserCommentConstan.CHECK_BY);
			newComment.setDownCount(0);
			newComment.setUpCount(0);
			newComment.setReplyTime(null);
			newComment.setParentId(userComment.getId());
			newComment.setReplyCommentId(userComment.getId());
			newComment.setReplyComment(userComment);
			newComment.setIsReport(false);
			newComment.setIsTop(false);
			newComment.setIsReply(true);
			newComment.setSortNum(10);
			newComment.setUser(user);
			newComment.setParent(userComment);
			newComment.setSite(site);
			newComment.setContent(content);
			UserComment bean = super.save(newComment);
			super.flush();

			//修改被回复的那个数据
			userComment.setReplyTime(new Date());
			userComment.setReplyAdminCommentId(bean.getId());
			userComment.setReplyAdminComment(bean);
			userComment.getChildren().add(bean);
			/*
			 * 新增评论回复后更新content的数量缓存
			 */
			contentFrontService.saveOrUpdateNum(contentId, null, ContentConstant.CONTENT_NUM_TYPE_COMMENTS, false);
		}
		super.update(userComment);
	}
	
	@Override
	public void deleted(List<UserComment> userComments, Integer[] ids) throws GlobalException {
		List<Integer> idList = new ArrayList<Integer>();
		Collections.addAll(idList, ids);
		List<UserComment> parentIds = dao.findByParentIdInAndHasDeleted(idList, false);
		if (parentIds != null && parentIds.size() > 0) {
			userComments.addAll(parentIds);
		}
		List<UserComment> replys = dao.findByReplyCommentIdInAndHasDeleted(idList, false);
		for (UserComment userComment : replys) {
			if (!userComments.contains(userComment)) {
				userComments.add(userComment);
			}
		}
		super.delete(userComments);
		Map<Integer,Integer> contentMap = userComments.stream().collect(Collectors.groupingBy(UserComment::getContentId, Collectors.summingInt(v->1)));
		for (Integer contentId : contentMap.keySet()) {
			contentFrontService.saveOrUpdateNum(contentId, contentMap.get(contentId), ContentConstant.CONTENT_NUM_TYPE_COMMENTS, true);
		}
	}
	
	@Override
	public void updateCheck(UserCommentDto dto, Integer siteId) throws GlobalException {
		Short status = dto.getStatus();
		List<UserComment> uList = super.findAllById(Arrays.asList(dto.getIds()));
		Short checkStatus = null;
		for (UserComment userComment : uList) {
			// 校验如果传入的评论的站点Id与controller层获取到的不一致，则说明有问题，则直接抛出异常
			if (!siteId.equals(userComment.getSiteId())) {
				throw new GlobalException(
						new SystemExceptionInfo(
						SettingErrorCodeEnum.COMMENT_IS_NOT_SITE_DATA.getDefaultMessage(),
							SettingErrorCodeEnum.COMMENT_IS_NOT_SITE_DATA.getCode()));
			}
			// 获取到该评论的状态
			checkStatus = userComment.getStatus();
			switch (status) {
				// 审核通过
				case 1:
					// 审核通过的前提必须是待审核或者是审核不通过，否则不进行修改
					if (UserCommentConstan.CHECK_WAIT.equals(checkStatus)
							||
                            UserCommentConstan.CHECK_FAIL.equals(checkStatus)) {
						userComment.setStatus(UserCommentConstan.CHECK_BY);
						contentFrontService.saveOrUpdateNum(userComment.getContentId(), 1, ContentConstant.CONTENT_NUM_TYPE_COMMENTS, false);
						if (userComment.getUserId() != null) {
							memberScoreDetailsService.addMemberScore(MemberScoreDetails.COMMENT_SCORE_TYPE,
									userComment.getUserId(), userComment.getSiteId(),userComment.getId());
						}
					}
					break;
				// 审核不通过
				case 2:
					// 审核不通过的前提必须是待审核
					if (UserCommentConstan.CHECK_WAIT.equals(checkStatus)) {
						userComment.setStatus(UserCommentConstan.CHECK_FAIL);
					}
					break;
				// 审核撤回
				case 3:
					// 审核撤回的前提必须是审核通过
					if (UserCommentConstan.CHECK_BY.equals(checkStatus)) {
						userComment.setStatus(UserCommentConstan.CHECK_WAIT);
						contentFrontService.saveOrUpdateNum(userComment.getContentId(), 1, ContentConstant.CONTENT_NUM_TYPE_COMMENTS, true);
					}
					break;
				default:
					break;
			}
		}
		super.batchUpdate(uList);
	}

	@Override
	public synchronized void saveStop(UserCommentDto dto, Integer siteId) throws GlobalException {
	    /*
	     *此处是并发压测时发现在并发情况下，出现极端情况即：下个线程进入该方法时新增还未进入数据库，然后后续就会出现同一条数据压测到数据库会出现多条记录
	     *所以此处进行睡眠
	     */
	    try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		SysBlackList sBlackList = null;
		UserComment userComment = super.findById(dto.getId());
		// 如果评论对象为空直接return出去
		if (userComment == null) {
		    return;
        }
		// 如果是禁止的是IP
		if (dto.getIsIp()) {
		    // 那么校验IP是否在数据库中
			if (sListService.checkUserComment(siteId, null, userComment.getIp())) {
			    return;
			}
            sBlackList = new SysBlackList();
			sBlackList.setIp(userComment.getIp());
		} else {
		    // 如果评论对象中不存在评论用户(用户不登录也可以评论)，然后继续校验userId是否在数据库中
			if (userComment.getUserId() == null || sListService.checkUserComment(siteId, userComment.getUserId(), null)) {
                return;
			}
            sBlackList = new SysBlackList();
			sBlackList.setUserId(userComment.getUserId());
			sBlackList.setUserName(userComment.getUser().getUsername());
		}
		sBlackList.setSiteId(siteId);
		sBlackList.setSite(siteService.findById(siteId));
		sBlackList.setType(SysBlackList.USER_COMMENT_TYPE);
		sListService.save(sBlackList);
	}

	@Override
	public synchronized void deleteStop(UserCommentDto dto, Integer siteId) throws GlobalException {
        /*
         *此处是并发压测时发现在并发情况下，出现极端情况即：下个线程进入该方法时新增还未进入数据库，然后后续就会出现同一条数据压测到数据库会出现多条记录
         *所以此处进行睡眠
         */
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		UserComment userComment = super.findById(dto.getId());
		if (userComment == null) {
		    return;
        }
		SysBlackList sysBlackList = null;
		if (dto.getIsIp()) {
			sysBlackList = sListService.findByUserIdByIp(siteId, SysBlackList.USER_COMMENT_TYPE,
					null, userComment.getIp());
		} else {
			sysBlackList = sListService.findByUserIdByIp(siteId, SysBlackList.USER_COMMENT_TYPE, 
					userComment.getUserId(), null);
		}
		if (sysBlackList != null) {
			sListService.physicalDelete(sysBlackList);
		}
	}

	@Override
	public List<UserComment> findByUserId(Integer userId) {
		return dao.findByUserIdAndHasDeleted(userId,false);
	}

	@Override
	public List<UserComment> findByIp(String ip) {
		return dao.findByIpAndHasDeleted(ip,false);
	}

	@Override
	public long getCount(Date beginTime, Date endTime, Integer siteId, Short status) {
		return dao.getCount(beginTime, endTime, siteId, status);
	}

	@Override
	public UserCommentCountVo getCount(Integer contentId,Integer siteId) throws GlobalException {
        UserCommentCountVo vo = new UserCommentCountVo(0, 0, 0, 0, 0);
        // 如果内容id为空只需要查询出举报列表数量
        if (contentId != null) {
            Content content = contentService.findById(contentId);
            if (content == null) {
                throw new GlobalException(ContentErrorCodeEnum.CONTENT_ID_PASSED_ERROR);
            }
            vo.setContentName(content.getTitle());
            vo.setChannelName(content.getChannelName());
            vo.setAllNum(dao.getCount(siteId, contentId, UserCommentConstan.COUNT_TYPE_ALL));
            vo.setPendingReviewNum(dao.getCount(siteId, contentId, UserCommentConstan.COUNT_TYPE_CHECK_WAIT));
            vo.setSuccessReviewNum(dao.getCount(siteId, contentId, UserCommentConstan.COUNT_TYPE_CHECK_BY));
            vo.setErrorReviewNum(dao.getCount(siteId, contentId, UserCommentConstan.COUNT_TYPE_CHECK_FAIL));
        } else {
            vo.setReportNum(dao.getCount(siteId, contentId, UserCommentConstan.COUNT_TYPE_REPORT));
        }
		return vo;
	}
	
	@Override
	public void top(UserCommentTopDto dto) throws GlobalException {
		UserComment userComment = super.findById(dto.getId());
		if (userComment == null) {
			return;
		}
		userComment.setIsTop(dto.getTop());
		super.update(userComment);
	}
	
	@Override
	public Page<UserInteractionVo> interactPage(Date startTime, Date endTime, Integer userId,
			Pageable pageable) throws GlobalException {
		List<Integer> integers = new ArrayList<Integer>(10);
		//查询我评论和回复的列表
		List<UserComment> list = dao.getInteractions(startTime, endTime, userId, null);
		List<UserInteractionVo> vos = new ArrayList<UserInteractionVo>(10);
		//分析出互动类型,得到我的评论和我的回复
		for (UserComment userComment : list) {
			UserInteractionVo vo = new UserInteractionVo();
			//回复ID不为空则为我的回复
			if (userComment.getReplyCommentId() != null) {
				vo.setUsername(userComment.getReplyComment().getUser() != null
						? userComment.getReplyComment().getUser().getUsername()
						: "");
				vo.setType(UserInteractionVo.TYPE_2);
			} else {
				vo.setUsername(userComment.getUser() != null 
						? userComment.getUser().getUsername()
						: "");
				vo.setType(UserInteractionVo.TYPE_1);
			}
			vo.setCommentTime(MyDateUtils.formatDate(userComment.getCreateTime(), 
					MyDateUtils.COM_Y_M_D_H_M_PATTERN));
			vo.setContentId(userComment.getContentId());
			vo.setContentUrl(userComment.getContent().getUrlWhole());
			vo.setId(userComment.getId());
			vo.setText(userComment.getCommentText());
			vo.setTitle(userComment.getContent().getTitle());
			vo.setUpCount(userComment.getUpCount());
			vo.setParentId(userComment.getParentId());
			vos.add(vo);
			integers.add(userComment.getId());
		}
		if (!integers.isEmpty()) {
			//得到回复我的
			List<UserComment> reply = dao.getInteractions(null, null, null, integers);
			for (UserComment userComment : reply) {
				UserInteractionVo vo = new UserInteractionVo();
				vo.setCommentTime(MyDateUtils.formatDate(userComment.getCreateTime(), 
						MyDateUtils.COM_Y_M_D_H_M_PATTERN));
				vo.setContentId(userComment.getContentId());
				vo.setContentUrl(userComment.getContent().getUrlWhole());
				vo.setId(userComment.getId());
				vo.setText(userComment.getCommentText());
				vo.setTitle(userComment.getContent().getTitle());
				vo.setType(UserInteractionVo.TYPE_3);
				vo.setUsername(userComment.getUser() != null 
						? userComment.getUser().getUsername()
						: "");
				vo.setUpCount(userComment.getUpCount());
				vo.setParentId(userComment.getParentId());
				vos.add(vo);
			}
		}
		PageImpl<UserInteractionVo> page;
		if (!vos.isEmpty()) {
			Integer sum = vos.size();
			vos = vos.stream()
					.skip(pageable.getPageSize() * (pageable.getPageNumber()))
					.limit(pageable.getPageSize()).collect(Collectors.toList());
			page = new PageImpl<>(vos, pageable, sum);
		} else {
			page = new PageImpl<>(vos, pageable, vos.size());
		}
		return page;
	}
	
	@Override
	public Page<UserInteractionMoblieVo> mobilePage(Integer userId, Pageable pageable) throws GlobalException {
		List<Integer> likes = new ArrayList<>(10);
		List<Integer> integers = new ArrayList<>(10);
		CoreUser user = coreUserService.findById(SystemContextUtils.getCoreUser().getId());
		//得到我点赞的评论
		if (user.getLikeComments() != null && !user.getLikeComments().isEmpty()) {
			likes = user.getLikeComments().stream().map(UserComment::getId).collect(Collectors.toList());
		}
		//查询我评论和回复的列表
		List<UserComment> list = dao.getInteractions(null, null, userId, null);
		List<UserInteractionMoblieVo> vos = new ArrayList<UserInteractionMoblieVo>(10);
		//分析出互动类型,得到我的评论和我的回复
		for (UserComment userComment : list) {
			if (userComment == null) {
				break;
			}
			UserInteractionMoblieVo vo = new UserInteractionMoblieVo();
			//回复ID不为空则为我的回复
			if (userComment.getReplyCommentId() != null) {
				if (userComment.getReplyComment() != null) {
					vo.setReplyUsername(userComment.getReplyComment().getUser() != null
						? userComment.getReplyComment().getUser().getUsername()
						: "");
				} else {
					vo.setReplyUsername("");
				}
				vo.setType(UserInteractionVo.TYPE_2);
			} else {
				vo.setType(UserInteractionVo.TYPE_1);
			}
			vo.setUsername(user.getUsername());
			vo.setCommentTime(userComment.getDistanceTime());
			vo.setCommentTimes(userComment.getCreateTime());
			vo.setId(userComment.getId());
			vo.setText(userComment.getCommentText());
			vo.setUpCount(userComment.getUpCount());
			vo.setParentId(userComment.getParentId());
			ContentFrontVo moblie = contentFrontService.initMobileVo(new ContentFrontVo(),
					userComment.getContent());
			vo.setMobileContent(moblie);
			vo.setHeadImage(user.getHeadImage());
			if (likes.contains(userComment.getId())) {
				vo.setLike(true);
			} else {
				vo.setLike(false);
			}
			vos.add(vo);
			integers.add(userComment.getId());
		}
		if (!integers.isEmpty()) {
			//得到回复我的
			List<UserComment> reply = dao.getInteractions(null, null, null, integers);
			for (UserComment userComment : reply) {
				UserInteractionMoblieVo vo = new UserInteractionMoblieVo();
				vo.setCommentTime(userComment.getDistanceTime());
				vo.setCommentTimes(userComment.getCreateTime());
				vo.setId(userComment.getId());
				vo.setText(userComment.getCommentText());
				vo.setType(UserInteractionVo.TYPE_3);
				vo.setUsername(userComment.getUser() != null 
						? userComment.getUser().getUsername()
						: "");
				vo.setUpCount(userComment.getUpCount());
				vo.setParentId(userComment.getParentId());
				ContentFrontVo moblie = contentFrontService.initMobileVo(new ContentFrontVo(),
						userComment.getContent());
				vo.setMobileContent(moblie);
				vo.setReplyUsername(user.getUsername());
				vo.setHeadImage(userComment.getUser() != null
						? userComment.getUser().getHeadImage()
								: "");
				if (likes.contains(userComment.getId())) {
					vo.setLike(true);
				} else {
					vo.setLike(false);
				}
				vos.add(vo);
			}
		}
		PageImpl<UserInteractionMoblieVo> page = null;
		if (!vos.isEmpty()) {
			vos = vos.stream().sorted(Comparator.comparing(UserInteractionMoblieVo::getCommentTimes)
					.reversed())
					.collect(Collectors.toList());
			Integer sum = vos.size();
			vos = vos.stream()
					.skip(pageable.getPageSize() * (pageable.getPageNumber()))
					.limit(pageable.getPageSize()).collect(Collectors.toList());
			page = new PageImpl<>(vos,pageable, sum);
		} else {
			page = new PageImpl<>(vos,pageable,vos.size());
		}
		return page;
	}
	
	@Override
	public List<UserComment> getListByReplyCommentId(List<Integer> ids) {
		return dao.findByReplyCommentIdInAndHasDeleted(ids, false);
	}
	
	@Override
	public ResponseInfo clear(Integer userId) throws GlobalException {
		List<Integer> integers = new ArrayList<>(10);
		List<UserComment> sum = new ArrayList<>(10);
		//查询我评论和回复的列表
		List<UserComment> list = dao.getInteractions(null, null, userId, null);
		if (!list.isEmpty()) {
			integers = list.stream().map(UserComment::getId).collect(Collectors.toList());
			sum.addAll(list);
		}
		if (!integers.isEmpty()) {
			//得到回复我的
			List<UserComment> reply = dao.getInteractions(null, null, null, integers);
			sum.addAll(reply);
		}
		super.delete(sum);
		return new ResponseInfo();
	}
	
	@Override
	public String getVersionArea(String ip) {
		StringBuffer buffer = new StringBuffer();
		// 省份
		String province = null;
		// 市区
		String city = null;
		Location location = null;
		AdInfo adInfo = null;
		try {
			location = addressService.getAddressByIP(ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (location != null && location.getResult() != null) {
			adInfo = location.getResult().getAdInfo();
			province = adInfo.getProvince();
			if (StringUtils.isNotBlank(province)) {
				buffer.append(province);
				city = adInfo.getCity();
				if (StringUtils.isNotBlank(city)) {
					buffer.append("-").append(city);
				}
			} else {
				buffer.append("其它");
			}
		} else {
			buffer.append("其它");
		}
		return buffer.toString();
	}

	@Override
	public List<ContentLikeVo> count(boolean type, Date start, Date end) {
		return dao.count(type, start, end);
	}

	@Override
	public List<MassScoreVo> massCount(List<Integer> contentId, Date startDate, Date endDate) {
		return dao.massCount(contentId, startDate, endDate);
	}

	@Override
	public List<UserComment> list(List<Integer> contentId, Date startDate, Date endDate, String province) {
		return dao.list(contentId, startDate, endDate, province);
	}

    @Override
    public synchronized void deleteReport(List<Integer> ids) throws GlobalException {
        /*
         *此处是并发压测时发现在并发情况下，出现极端情况即：下个线程进入该方法时新增还未进入数据库，然后后续就会出现同一条数据压测到数据库会出现多条记录
         *所以此处进行睡眠
         */
	    try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    List<UserComment> uList = super.findAllById(ids);
        if (CollectionUtils.isEmpty(uList)) {
            return;
        }
        for (UserComment userComment : uList) {
            userComment.setIsReport(false);
        }
        super.batchUpdate(uList);
        List<UserCommentReport> reports = reportService.findByCommentId(ids);
        if (CollectionUtils.isEmpty(reports)) {
            return;
        }
        reportService.physicalDeleteInBatch(reports);
    }

    @Override
    public long findBySiteAndIds(Integer siteId, List<Integer> ids) {
        return dao.getCount(siteId, ids);
    }

    @Autowired
	private SysBlackListService sListService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private ContentFrontService contentFrontService;
	@Autowired
	private UserCommentReportService commentReportService;
	@Autowired
	private MemberScoreDetailsService memberScoreDetailsService;
	@Autowired
	private CoreUserService coreUserService;
	@Autowired
	private AddressService addressService;
    @Autowired
    private CmsSiteService siteService;
    @Autowired
    private UserCommentReportService reportService;

}