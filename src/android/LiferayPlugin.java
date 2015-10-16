/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
*/

package salvatejero.cordova.liferay;

/**
 * Created by stejeros on 21/12/2014.
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.liferay.mobile.android.auth.basic.BasicAuthentication;
import com.liferay.mobile.android.service.BaseService;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.service.SessionImpl;
import com.liferay.mobile.android.task.callback.typed.JSONArrayAsyncTaskCallback;
import com.liferay.mobile.android.task.callback.typed.JSONObjectAsyncTaskCallback;
import com.liferay.mobile.android.v62.address.AddressService;
import com.liferay.mobile.android.v62.assetcategory.AssetCategoryService;
import com.liferay.mobile.android.v62.assetentry.AssetEntryService;
import com.liferay.mobile.android.v62.assettag.AssetTagService;
import com.liferay.mobile.android.v62.assetvocabulary.AssetVocabularyService;
import com.liferay.mobile.android.v62.blogsentry.BlogsEntryService;
import com.liferay.mobile.android.v62.bookmarksentry.BookmarksEntryService;
import com.liferay.mobile.android.v62.bookmarksfolder.BookmarksFolderService;
import com.liferay.mobile.android.v62.company.CompanyService;
import com.liferay.mobile.android.v62.contact.ContactService;
import com.liferay.mobile.android.v62.country.CountryService;
import com.liferay.mobile.android.v62.ddlrecord.DDLRecordService;
import com.liferay.mobile.android.v62.ddlrecordset.DDLRecordSetService;
import com.liferay.mobile.android.v62.ddmstructure.DDMStructureService;
import com.liferay.mobile.android.v62.ddmtemplate.DDMTemplateService;
import com.liferay.mobile.android.v62.dlfileentry.DLFileEntryService;
import com.liferay.mobile.android.v62.dlfileentrytype.DLFileEntryTypeService;
import com.liferay.mobile.android.v62.dlfileversion.DLFileVersionService;
import com.liferay.mobile.android.v62.dlfolder.DLFolderService;
import com.liferay.mobile.android.v62.emailaddress.EmailAddressService;
import com.liferay.mobile.android.v62.expandocolumn.ExpandoColumnService;
import com.liferay.mobile.android.v62.expandovalue.ExpandoValueService;
import com.liferay.mobile.android.v62.group.GroupService;
import com.liferay.mobile.android.v62.image.ImageService;
import com.liferay.mobile.android.v62.journalarticle.JournalArticleService;
import com.liferay.mobile.android.v62.journalfeed.JournalFeedService;
import com.liferay.mobile.android.v62.journalfolder.JournalFolderService;
import com.liferay.mobile.android.v62.layout.LayoutService;
import com.liferay.mobile.android.v62.layoutbranch.LayoutBranchService;
import com.liferay.mobile.android.v62.layoutprototype.LayoutPrototypeService;
import com.liferay.mobile.android.v62.layoutrevision.LayoutRevisionService;
import com.liferay.mobile.android.v62.layoutset.LayoutSetService;
import com.liferay.mobile.android.v62.layoutsetprototype.LayoutSetPrototypeService;
import com.liferay.mobile.android.v62.listtype.ListTypeService;
import com.liferay.mobile.android.v62.mbban.MBBanService;
import com.liferay.mobile.android.v62.mbcategory.MBCategoryService;
import com.liferay.mobile.android.v62.mbmessage.MBMessageService;
import com.liferay.mobile.android.v62.mbthread.MBThreadService;
import com.liferay.mobile.android.v62.mdraction.MDRActionService;
import com.liferay.mobile.android.v62.mdrrule.MDRRuleService;
import com.liferay.mobile.android.v62.mdrrulegroup.MDRRuleGroupService;
import com.liferay.mobile.android.v62.mdrrulegroupinstance.MDRRuleGroupInstanceService;
import com.liferay.mobile.android.v62.membershiprequest.MembershipRequestService;
import com.liferay.mobile.android.v62.organization.OrganizationService;
import com.liferay.mobile.android.v62.orglabor.OrgLaborService;
import com.liferay.mobile.android.v62.passwordpolicy.PasswordPolicyService;
import com.liferay.mobile.android.v62.permission.PermissionService;
import com.liferay.mobile.android.v62.phone.PhoneService;
import com.liferay.mobile.android.v62.portal.PortalService;
import com.liferay.mobile.android.v62.portlet.PortletService;
import com.liferay.mobile.android.v62.portletpreferences.PortletPreferencesService;
import com.liferay.mobile.android.v62.repository.RepositoryService;
import com.liferay.mobile.android.v62.resourcepermission.ResourcePermissionService;
import com.liferay.mobile.android.v62.role.RoleService;
import com.liferay.mobile.android.v62.team.TeamService;
import com.liferay.mobile.android.v62.user.UserService;
import com.liferay.mobile.android.v62.usergroup.UserGroupService;
import com.liferay.mobile.android.v62.usergroupgrouprole.UserGroupGroupRoleService;
import com.liferay.mobile.android.v62.usergrouprole.UserGroupRoleService;
import com.liferay.mobile.android.v62.wikinode.WikiNodeService;
import com.liferay.mobile.android.v62.wikipage.WikiPageService;

public class LiferayPlugin extends CordovaPlugin {

	private static final String TAG = "LIFERAY_PLUGIN";
	private static final String ACTION_CONNECT = "connect";
	private static final String GET_CONNECT = "execute";

	private static Session session;

	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) {

		Log.i(TAG, action);
		try {
			if (ACTION_CONNECT.equals(action)) {
				String userName = args.getString(1);
				String serverIp = args.getString(0);
				String password = args.getString(2);

				doConnect(callbackContext, serverIp, userName, password);
				return true;
				
			} else if(GET_CONNECT.equals(action)) {
				String classNameId = args.getString(0);
				getObjectModel(callbackContext, classNameId , args.getString(1), args.getJSONArray(2));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			callbackContext.error("Params error");
			Log.e(TAG, "Params error");
			return false;
		}
	}

	private void getObjectModel(final CallbackContext callbackContext, String className, String methodName, JSONArray values) throws Exception{
		
		JSONArray jsonArrayInstance = new JSONArray();
		JSONObject jsonObjectInstance = new JSONObject();
		
		JSONObjectAsyncTaskCallback callBackJSONObject = new JSONObjectAsyncTaskCallback() {
			
			@Override
			public void onFailure(Exception arg0) {
				callbackContext.error(arg0.getMessage());
			}
			
			@Override
			public void onSuccess(JSONObject arg0) {
				// TODO Auto-generated method stub
				PluginResult pluginResult = new PluginResult(
						PluginResult.Status.OK, arg0);
				pluginResult.setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult);
			}

		};
		
		JSONArrayAsyncTaskCallback callbackJSONArray = new JSONArrayAsyncTaskCallback() {
			
			@Override
			public void onFailure(Exception arg0) {
				callbackContext.error(arg0.getMessage());				
			}
			
			@Override
			public void onSuccess(JSONArray arg0) {
				PluginResult pluginResult = new PluginResult(
						PluginResult.Status.OK, arg0);
				pluginResult.setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult);
				
			}
		};
		
		Method methodToExecute = null;
		Object[] params = null;
		BaseService service = getService(className);
		
		if(service == null){
			throw new LiferayPluginException("Service not implemented");
		}
		Method[] methods = service.getClass().getMethods();
		for(Method m: methods){
			if(m.getName().toLowerCase().equals(methodName.toLowerCase())){
				
				if(values.length() != m.getParameterTypes().length){
					throw new LiferayPluginException("Number of params error for the method " + methodName);
				}
				params = getListOfParam(m, values);
				if(m.getReturnType().isInstance(jsonArrayInstance)){
					session.setCallback(callbackJSONArray);
				}else if (m.getReturnType().isInstance(jsonObjectInstance)){
					session.setCallback(callBackJSONObject);
				}else if( m.getReturnType().equals(Void.TYPE)){
					callbackContext.success();
				}
				
				methodToExecute = m;
				break;
			}
		}
        if(methodToExecute == null) {
            for (Method m : methods) {
                if (methodName.indexOf(m.getName().toLowerCase()) >= 0) {
                    
                    if (values.length() != m.getParameterTypes().length) {
                        throw new LiferayPluginException("Number of params error for the method " + methodName);
                    }
                    params = getListOfParam(m, values);
                    if (m.getReturnType().isInstance(jsonArrayInstance)) {
                        session.setCallback(callbackJSONArray);
                    } else if (m.getReturnType().isInstance(jsonObjectInstance)) {
                        session.setCallback(callBackJSONObject);
                    } else if (m.getReturnType().equals(Void.TYPE)) {
                        callbackContext.success();
                    }
                    
                    methodToExecute = m;
                    break;
                }
            }
        }
		if(methodToExecute == null){
			throw new LiferayPluginException("Method " +methodName+ "not found");
		}
		
		try {
			methodToExecute.invoke(service, params);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new LiferayPluginException("Error invoking -- "+e.getMessage());
		}
		
	}
	
	
	
	private Object[] getListOfParam(Method m, JSONArray values) throws JSONException{
		List<Object> listOfParams = new ArrayList<Object>();
		for(int i=0; i< m.getParameterTypes().length; i++){
			@SuppressWarnings("rawtypes")
			Class c = m.getParameterTypes()[i];
			if(c.getName().equals("java.lang.String")){
				listOfParams.add(values.getString(i));
			}else if(c.getName().equals("java.lang.Long")){
				listOfParams.add(values.getLong(i));
			}else if(c.getName().equals("java.lang.Integer")){
				listOfParams.add(values.getInt(i));
			}else if(c.getName().equals("long")){
				listOfParams.add(values.getLong(i));
			}else if(c.getName().equals("int")){
				listOfParams.add(values.getInt(i));
			}
		}
		Object[] paramsA = new Object[listOfParams.size()];
		return listOfParams.toArray(paramsA);
	}
	
	private void doConnect(final CallbackContext callbackContext,
			final String urlServer, final String userName, final String password) {

		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				session = new SessionImpl(urlServer, new BasicAuthentication(userName, password));
				try {
					JSONObject user = getUser(session, userName);
					PluginResult pluginResult = new PluginResult(
							PluginResult.Status.OK, user);
					pluginResult.setKeepCallback(true);
					callbackContext.sendPluginResult(pluginResult);
				} catch (Exception e) {
					callbackContext.error(e.getMessage());
					
				}
				
			}
		});

	}

	
	protected JSONObject getUser(Session session, String username) throws Exception {
		UserService userService = new UserService(session);
		
		JSONObject user = userService.getUserByEmailAddress(preferences.getInteger("liferay-company-default", 10154), username);
		return user;	
	}
	
	protected JSONObject getGuestGroupId(Session session) throws Exception {
		long groupId = -1;
		GroupService groupService = new GroupService(session);
		JSONArray groups = groupService.getUserSites();
		for (int i = 0; i < groups.length(); i++) {
			JSONObject group = groups.getJSONObject(i);
			String name = group.getString("name");
			if (!name.equals("Guest")) {
				continue;
			}
			return group;
		}
		if (groupId == -1) {
			throw new Exception("Couldn't find Guest group.");
		}
		return null;
	}
	
	private BaseService getService(String className){
		BaseService service = null;
		if(className.equals("com.liferay.portal.model.User")){
			service = new UserService(session);
		}else if(className.equals("com.liferay.portal.model.Address")){
			service = new AddressService(session);
		}else if(className.equals("com.liferay.portlet.asset.model.AssetCategory")){
			service = new AssetCategoryService(session);
		}else if(className.equals("com.liferay.portlet.asset.model.AssetEntry")){
			service = new AssetEntryService(session);
		}else if(className.equals("com.liferay.portlet.asset.model.AssetTag")){
			service = new AssetTagService(session);
		}else if(className.equals("com.liferay.portlet.asset.model.AssetVocabulary")){
			service = new AssetVocabularyService(session);
		}else if(className.equals("com.liferay.portlet.blogs.model.BlogsEntry")){
			service = new BlogsEntryService(session);
		}else if(className.equals("com.liferay.portlet.bookmarks.model.BookmarksEntry")){
			service = new BookmarksEntryService(session);
		}else if(className.equals("com.liferay.portlet.bookmarks.model.BookmarksFolder")){
			service = new BookmarksFolderService(session);
		}else if(className.equals("com.liferay.portal.model.Company")){
			service = new CompanyService(session);
		}else if(className.equals("com.liferay.portal.model.Contact")){
			service = new ContactService(session);
		}else if(className.equals("com.liferay.portal.model.Country")){
			service = new CountryService(session);
		}else if(className.equals("com.liferay.portlet.dynamicdatalists.model.DDLRecord")){
			service = new DDLRecordService(session);
		}else if(className.equals("com.liferay.portlet.dynamicdatalists.model.DDLRecordSet")){
			service = new DDLRecordSetService(session);
		}else if(className.equals("com.liferay.portlet.dynamicdatamapping.model.DDMStructure")){
			service = new DDMStructureService(session);
		}else if(className.equals("com.liferay.portlet.dynamicdatamapping.model.DDMTemplate")){
			service = new DDMTemplateService(session);
		}else if(className.equals("com.liferay.portlet.documentlibrary.model.DLFileEntry")){
			service = new DLFileEntryService(session);
		}else if(className.equals("com.liferay.portlet.documentlibrary.model.DLFileEntryType")){
			service = new DLFileEntryTypeService(session);
		}else if(className.equals("com.liferay.portlet.documentlibrary.model.DLFileVersion")){
			service = new DLFileVersionService(session);
		}else if(className.equals("com.liferay.portlet.documentlibrary.model.DLFolder")){
			service = new DLFolderService(session);
		}else if(className.equals("com.liferay.portal.model.EmailAddress")){
			service = new EmailAddressService(session);
		}else if(className.equals("com.liferay.portlet.expando.model.ExpandoColumn")){
			service = new ExpandoColumnService(session);
		}else if(className.equals("com.liferay.portlet.expando.model.ExpandoValue")){
			service = new ExpandoValueService(session);
		}else if(className.equals("com.liferay.portal.model.Group")){
			service = new GroupService(session);
		}else if(className.equals("com.liferay.portal.model.Image")){
			service = new ImageService(session);
		}else if(className.equals("com.liferay.portlet.journal.model.JournalArticle")){
			service = new JournalArticleService(session);
		}else if(className.equals("com.liferay.portlet.journal.model.JournalFeed")){
			service = new JournalFeedService(session);
		}else if(className.equals("com.liferay.portlet.journal.model.JournalFolder")){
			service = new JournalFolderService(session);
		}else if(className.equals("com.liferay.portal.model.Layout")){
			service = new LayoutService(session);
		}else if(className.equals("com.liferay.portal.model.LayoutBranch")){
			service = new LayoutBranchService(session);
		}else if(className.equals("com.liferay.portal.model.LayoutPrototype")){
			service = new LayoutPrototypeService(session);
		}else if(className.equals("com.liferay.portal.model.LayoutRevision")){
			service = new LayoutRevisionService(session);
		}else if(className.equals("com.liferay.portal.model.LayoutSet")){
			service = new LayoutSetService(session);
		}else if(className.equals("com.liferay.portal.model.LayoutSetPrototype")){
			service = new LayoutSetPrototypeService(session);
		}else if(className.equals("com.liferay.portal.model.ListType")){
			service = new ListTypeService(session);
		}else if(className.equals("com.liferay.portlet.messageboards.model.MBBan")){
			service = new MBBanService(session);
		}else if(className.equals("com.liferay.portlet.messageboards.model.MBCategory")){
			service = new MBCategoryService(session);
		}else if(className.equals("com.liferay.portlet.messageboards.model.MBMessage")){
			service = new MBMessageService(session);
		}else if(className.equals("com.liferay.portlet.messageboards.model.MBThread")){
			service = new MBThreadService(session);
		}else if(className.equals("com.liferay.portlet.mobiledevicerules.model.MDRAction")){
			service = new MDRActionService(session);
		}else if(className.equals("com.liferay.portlet.mobiledevicerules.model.MDRRule")){
			service = new MDRRuleService(session);
		}else if(className.equals("com.liferay.portlet.mobiledevicerules.model.MDRRuleGroup")){
			service = new MDRRuleGroupService(session);
		}else if(className.equals("com.liferay.portlet.mobiledevicerules.model.MDRRuleGroupInstance")){
			service = new MDRRuleGroupInstanceService(session);
		}else if(className.equals("com.liferay.portal.model.MembershipRequest")){
			service = new MembershipRequestService(session);
		}else if(className.equals("com.liferay.portal.model.Organization")){
			service = new OrganizationService(session);
		}else if(className.equals("com.liferay.portal.model.OrgLabor")){
			service = new OrgLaborService(session);
		}else if(className.equals("com.liferay.portal.model.PasswordPolicy")){
			service = new PasswordPolicyService(session);
		}else if(className.equals("Permission")){
			service = new PermissionService(session);
		}else if(className.equals("com.liferay.portal.model.Phone")){
			service = new PhoneService(session);
		}else if(className.equals("Portal")){
			service = new PortalService(session);
		}else if(className.equals("com.liferay.portal.model.Portlet")){
			service = new PortletService(session);
		}else if(className.equals("com.liferay.portal.model.PortletPreferences")){
			service = new PortletPreferencesService(session);
		}else if(className.equals("com.liferay.portal.model.Repository")){
			service = new RepositoryService(session);
		}else if(className.equals("com.liferay.portal.model.ResourcePermission")){
			service = new ResourcePermissionService(session);
		}else if(className.equals("com.liferay.portal.model.Role")){
			service = new RoleService(session);
		}else if(className.equals("com.liferay.portal.model.Team")){
			service = new TeamService(session);
		}else if(className.equals("com.liferay.portal.model.UserGroup")){
			service = new UserGroupService(session);
		}else if(className.equals("com.liferay.portal.model.UserGroupGroupRole")){
			service = new UserGroupGroupRoleService(session);
		}else if(className.equals("com.liferay.portal.model.UserGroupRole")){
			service = new UserGroupRoleService(session);
		}else if(className.equals("com.liferay.portlet.wiki.model.WikiNode")){
			service = new WikiNodeService(session);
		}else if(className.equals("com.liferay.portlet.wiki.model.WikiPage")){
			service = new WikiPageService(session);
		}
		
		return service;
	}
	
	public class LiferayPluginException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String message;
		
		public LiferayPluginException(String message){
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}

	}
}
