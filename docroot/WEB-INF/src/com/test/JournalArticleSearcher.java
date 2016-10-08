package com.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.model.UserGroupGroupRole;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * Portlet implementation class JournalArticleSearcher
 */
public class JournalArticleSearcher extends MVCPortlet {
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		System.out.println("* ProcessAction starts.. *");

		final long companyId = PortalUtil.getDefaultCompanyId();
		long defGroupId = 0;
		long groupId = 0;
		try {
			defGroupId = GroupLocalServiceUtil.getGroup(companyId, "Guest")
					.getGroupId();
			// groupId = GroupLocalServiceUtil.getGroup(companyId,
			// "TestSite1").getGroupId();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// final Role siteMemberRole = RoleLocalServiceUtil.getRole(companyId,
		// RoleConstants.SITE_MEMBER);
		User currentUser = null;
		List<Role> fetchedRoles = null;
		try {
			currentUser = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		if (currentUser == null) {
			currentUser = themeDisplay.getUser();
		}

		PermissionChecker permissionChecker = themeDisplay
				.getPermissionChecker();

		try {
			fetchedRoles = getRole(currentUser);
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Username of the currently logged on user: "
				+ currentUser.getScreenName());
		for (Role fetchedRole : fetchedRoles) {
			System.out.println("roles> " + fetchedRole.getName());
		}

//		int start = 0;
//		int end = 1000;
//		List<JournalArticle> myResults1 = new ArrayList<JournalArticle>();
//		List<JournalArticle> myResults2 = new ArrayList<JournalArticle>();
		//try {
//			myResults1 = JournalArticleLocalServiceUtil.getArticles(defGroupId,
//					start, end);
//			myResults1 = JournalArticleLocalServiceUtil.getArticles(defGroupId);
			// myResults2 = JournalArticleLocalServiceUtil.getArticles(groupId,
			// start, end);
//		} catch (SystemException e) {
//			e.printStackTrace();
//		}
		
		
		String myStructureKey = "";
		JournalArticle articleLastVersion;
		List<JournalArticle> latestArticleList = new ArrayList<JournalArticle>();
		List<JournalArticle> allArticlesList = null;
		try {
			allArticlesList = JournalArticleLocalServiceUtil.getStructureArticles(defGroupId, myStructureKey);
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ListIterator<JournalArticle> it = allArticlesList.listIterator();
		List<String> checkedArticleIds = new ArrayList<String>();
		 
		while (it.hasNext()) {
		  JournalArticle article = it.next();
		  if (checkedArticleIds.contains(article.getArticleId())) {
		    continue; // previous article version already checked
		  }
		  try {
			articleLastVersion = JournalArticleLocalServiceUtil.getLatestArticle(defGroupId, article.getArticleId());
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  checkedArticleIds.add(article.getArticleId());
//		  System.out.println("Added articleId " + article.getArticleId() + " with version " + article.getVersion());
		  latestArticleList.add(article);
		}
		
		
		
		for (JournalArticle myResult1 : latestArticleList) {

			try {
				JournalArticlePermission.check(permissionChecker, myResult1,
						ActionKeys.VIEW);
//				System.out.println("---myResult START: " + myResult1.getTitle());
				System.out.println(myResult1.getTitle());
			} catch (PortalException e) {
				// TODO Auto-generated catch block
//				System.out.println("NO PERMISSIONS!!");
				// e.printStackTrace();
			}

			// System.out.println("art " + myResult1.getTitle());

//			System.out.println("---myResult END ");

		}

		// final long roleId = RoleLocalServiceUtil.getRole(companyId,
		// RoleConstants.GUEST).getRoleId();
		// final Map<Long, String[]> roles = new HashMap<Long, String[]>();
		// roles.put(roleId, new String[] { ActionKeys.VIEW });
		// if
		// (!ResourcePermissionLocalServiceUtil.hasResourcePermission(companyId,
		// JournalArticle.class.getName(),
		// ResourceConstants.SCOPE_INDIVIDUAL,
		// Long.toString(model.getResourcePrimKey()), roleId, ActionKeys.VIEW))
		// {
		// ResourcePermissionLocalServiceUtil.setResourcePermissions(companyId,
		// JournalArticle.class.getName(),
		// ResourceConstants.SCOPE_INDIVIDUAL,
		// Long.toString(model.getResourcePrimKey()), roles);
		// }

		// for(JournalArticle myResult2 : myResults2){
		// System.out.println("---myResult2: " + myResult2);
		// }

		System.out.println("* ProcessAction ends.. *");
	}

	private List<Role> getRole(User currentUser) throws SystemException,
			PortalException {
		List<Role> roles = new ArrayList<Role>();
		roles.addAll(currentUser.getRoles());
		roles.addAll(getUserGroupRolesOfUser(currentUser));
		roles.addAll(getUserExplicitRoles(currentUser));
		return roles;
	}

	List<Role> getUserExplicitRoles(User user) throws SystemException,
			PortalException {
		List<Role> roles = new ArrayList<Role>();
		List<UserGroupRole> userGroupRoles = UserGroupRoleLocalServiceUtil
				.getUserGroupRoles(user.getUserId());
		for (UserGroupRole userGroupRole : userGroupRoles) {
			roles.add(userGroupRole.getRole());
		}
		return roles;
	}

	private static List<Role> getUserGroupRolesOfUser(User user)
			throws SystemException, PortalException {
		List<Role> roles = new ArrayList<Role>();
		List<UserGroup> userGroupList = UserGroupLocalServiceUtil
				.getUserUserGroups(user.getUserId());
		List<UserGroupGroupRole> userGroupGroupRoles = new ArrayList<UserGroupGroupRole>();
		for (UserGroup userGroup : userGroupList) {
			userGroupGroupRoles.addAll(UserGroupGroupRoleLocalServiceUtil
					.getUserGroupGroupRoles(userGroup.getUserGroupId()));
		}
		for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
			Role role = RoleLocalServiceUtil.getRole(userGroupGroupRole
					.getRoleId());
			roles.add(role);
		}
		return roles;
	}

}
