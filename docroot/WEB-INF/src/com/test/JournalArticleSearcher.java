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

		// Defines basic variables
		final long companyId = PortalUtil.getDefaultCompanyId();
		long defGroupId = 0;
		try {
			defGroupId = GroupLocalServiceUtil.getGroup(companyId, "Guest")
					.getGroupId();
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		User currentUser = null;
		try {
			currentUser = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		if (currentUser == null) {
			currentUser = themeDisplay.getUser();
		}

		PermissionChecker permissionChecker = themeDisplay
				.getPermissionChecker();

		System.out.println("Username of the currently logged on user: "
				+ currentUser.getScreenName());

		// Collects a list of journal articles, and this list will only contain the latest version of the articles.
		String myStructureKey = "";
		JournalArticle articleLastVersion;
		List<JournalArticle> latestArticleList = new ArrayList<JournalArticle>();
		List<JournalArticle> allArticlesList = null;
		try {
			allArticlesList = JournalArticleLocalServiceUtil
					.getStructureArticles(defGroupId, myStructureKey);
		} catch (SystemException e1) {
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
				articleLastVersion = JournalArticleLocalServiceUtil
						.getLatestArticle(defGroupId, article.getArticleId());
				latestArticleList.add(article);
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			}
			checkedArticleIds.add(article.getArticleId());
		}

		// Loops through the latest article list, and checks if the currently logged on user has view permissions
		for (JournalArticle myResult1 : latestArticleList) {
			try {
				JournalArticlePermission.check(permissionChecker, myResult1,
						ActionKeys.VIEW);
				System.out.println("The user has only view permissions on this article:");
				System.out.println(myResult1.getTitleCurrentValue());
				} catch (PortalException e) {
			}
		}

		System.out.println("* ProcessAction ends.. *");
	}
}
