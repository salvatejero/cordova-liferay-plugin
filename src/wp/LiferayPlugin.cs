/*  
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using Liferay.SDK;
using Liferay.SDK.Service.V62.User;
using System.Windows.Threading;
using System.Windows;
using System.Diagnostics;
using Newtonsoft.Json;
using Liferay.SDK.Service.V62.Address;
using Liferay.SDK.Service.V62.AssetCategory;
using Liferay.SDK.Service.V62.AssetEntry;
using Liferay.SDK.Service.V62.AssetTag;
using Liferay.SDK.Service.V62.AssetVocabulary;
using Liferay.SDK.Service.V62.BlogsEntry;
using Liferay.SDK.Service.V62.BookmarksEntry;
using Liferay.SDK.Service.V62.BookmarksFolder;
using Liferay.SDK.Service.V62.Company;
using Liferay.SDK.Service.V62.Contact;
using Liferay.SDK.Service.V62.Country;
using Liferay.SDK.Service.V62.DDLRecord;
using Liferay.SDK.Service.V62.DDLRecordSet;
using Liferay.SDK.Service.V62.DDMStructure;
using Liferay.SDK.Service.V62.DDMTemplate;
using Liferay.SDK.Service.V62.DLFileEntry;
using Liferay.SDK.Service.V62.DLFileEntryType;
using Liferay.SDK.Service.V62.DLFileVersion;
using Liferay.SDK.Service.V62.DLFolder;
using Liferay.SDK.Service.V62.EmailAddress;
using Liferay.SDK.Service.V62.ExpandoColumn;
using Liferay.SDK.Service.V62.ExpandoValue;
using Liferay.SDK.Service.V62.Group;
using Liferay.SDK.Service.V62.Image;
using Liferay.SDK.Service.V62.JournalArticle;
using Liferay.SDK.Service.V62.JournalFeed;
using Liferay.SDK.Service.V62.JournalFolder;
using Liferay.SDK.Service.V62.Layout;
using Liferay.SDK.Service.V62.LayoutBranch;
using Liferay.SDK.Service.V62.LayoutPrototype;
using Liferay.SDK.Service.V62.LayoutRevision;
using Liferay.SDK.Service.V62.LayoutSet;
using Liferay.SDK.Service.V62.LayoutSetPrototype;
using Liferay.SDK.Service.V62.ListType;
using Liferay.SDK.Service.V62.WikiPage;
using Liferay.SDK.Service.V62.WikiNode;
using Liferay.SDK.Service.V62.UserGroupRole;
using Liferay.SDK.Service.V62.UserGroupGroupRole;
using Liferay.SDK.Service.V62.UserGroup;
using Liferay.SDK.Service.V62.Team;
using Liferay.SDK.Service.V62.Role;
using Liferay.SDK.Service.V62.ResourcePermission;
using Liferay.SDK.Service.V62.Repository;
using Liferay.SDK.Service.V62.PortletPreferences;
using Liferay.SDK.Service.V62.Portlet;
using Liferay.SDK.Service.V62.Portal;
using Liferay.SDK.Service.V62.Phone;
using Liferay.SDK.Service.V62.Permission;
using Liferay.SDK.Service.V62.PasswordPolicy;
using Liferay.SDK.Service.V62.Organization;
using Liferay.SDK.Service.V62.OrgLabor;
using Liferay.SDK.Service.V62.MembershipRequest;
using Liferay.SDK.Service.V62.MDRRule;
using Liferay.SDK.Service.V62.MDRRuleGroup;
using Liferay.SDK.Service.V62.MDRRuleGroupInstance;
using Liferay.SDK.Service.V62.MDRAction;
using Liferay.SDK.Service.V62.MBThread;
using Liferay.SDK.Service.V62.MBMessage;
using Liferay.SDK.Service.V62.MBBan;
using Liferay.SDK.Service.V62.MBCategory;
using System.Reflection;

namespace WPCordovaClassLib.Cordova.Commands
{
    class LiferayPlugin : BaseCommand
    {
        private static Session _session;

        public void connect(string options)
        {
            string[] args = JSON.JsonHelper.Deserialize<string[]>(options);
            _session = new Session(new Uri(args[0]), args[1], args[2]);

            var user = getUser(_session, args[1]);
            var stringJson = Newtonsoft.Json.JsonConvert.SerializeObject(user);
            DispatchCommandResult(new PluginResult(PluginResult.Status.OK, stringJson));

        }

        public void execute(string options)
        {
            string[] args = JSON.JsonHelper.Deserialize<string[]>(options);
            string className = "";
            string methodName = "";
            string[] parameters = null;
            try
            { 
                className = args[0];
                methodName = args[1];
                parameters = JSON.JsonHelper.Deserialize<string[]>(args[2]);
            }catch(Exception e)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Param error"));
                return;
            }
            ServiceBase service = getService(className);
            if (service == null)
            {
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Service not found"));
                return;
            }
            MethodInfo methodToExecute = null;

            if (methodName.IndexOf("With") >= 0)
            {
               methodName = methodName.Substring(0, methodName.IndexOf("With"));
            }
            List<dynamic> listaParams = new List<dynamic>();
            MethodInfo[] methods = service.GetType().GetMethods();
 
            foreach(MethodInfo method in methods){
                if (method.Name.ToLower().Equals(methodName.ToLower())
                    || method.Name.ToLower().IndexOf(methodName.ToLower()) >= 0)
                {
                    ParameterInfo[] paramsInfo = method.GetParameters();
                    if (paramsInfo.Length != parameters.Length)
                    {
                        DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Number of params error for the method " + methodName));
                        return;
                    }
                    else 
                    {
                        methodToExecute = method;
                        listaParams = getParams(paramsInfo, parameters);
                        
                    }
                }
            }

            if (methodToExecute != null )
            {
                var retorno = methodToExecute.Invoke(service, listaParams.ToArray());
                try 
                { 
                    var stringJson = Newtonsoft.Json.JsonConvert.SerializeObject(retorno);
                    DispatchCommandResult(new PluginResult(PluginResult.Status.OK, stringJson));
                }
                catch(Exception ex)
                {
                    DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Error executing " + methodName + "    " +ex.Message));
                    return;
                }

            }
        }


        private List<dynamic> getParams(ParameterInfo[] paramsInfo, string[] parameters)
        {
            List<dynamic> listaParams = new List<dynamic>();
            for (int i = 0; i < paramsInfo.Length; i++)
            {
                if (paramsInfo[i].ParameterType.Name.IndexOf("Int64") >= 0)
                {
                    listaParams.Add(Convert.ToInt64(parameters[i]));
                }
                else if (paramsInfo[i].ParameterType.ToString().IndexOf("IEnumerable`1[System.Int64]") >= 0)
                {
                    char[] ca = parameters[i].ToArray();
                    long[] aL = new long[ca.Length];
                    for (int j = 0; j < ca.Length; j++)
                    {
                        char p = ca[j];
                        aL[j] = Convert.ToInt64(p);
                    }
                    listaParams.Add(aL);
                }
                else if (paramsInfo[i].ParameterType.ToString().IndexOf("IDictionary`2[System.String,System.Object]") >= 0)
                {
                    var ca = parameters[i];
                    Dictionary<string, object> values = JsonConvert.DeserializeObject<Dictionary<string, object>>(ca);
                    listaParams.Add(values);
                }
                else if (paramsInfo[i].ParameterType.ToString().IndexOf("IEnumerable`1[System.String]") >= 0)
                {
                    char[] ca = parameters[i].ToArray();
                    string[] aL = new string[ca.Length];
                    for (int j = 0; j < ca.Length; j++)
                    {
                        char p = ca[j];
                        aL[j] = Convert.ToString(p);
                    }
                    listaParams.Add(aL);
                }
                else if (paramsInfo[i].ParameterType.ToString().IndexOf("String") >= 0)
                {
                    listaParams.Add(parameters[i]);
                }
                else if (paramsInfo[i].ParameterType.ToString().IndexOf("Int32") >= 0)
                {
                    listaParams.Add(Convert.ToInt32(parameters[i]));
                }
            }

            return listaParams;
        }
        

        public dynamic getUser(Session session, string username)
        {
            var service = new UserService(_session);
            if(username.IndexOf("@")> 0){
                return  service.GetUserByEmailAddressAsync(10155, username).Result;
            }
            else
            {
               return service.GetUserByScreenNameAsync(10155, username).Result;
            }
        }

        private ServiceBase getService(string className)
        {
            ServiceBase service = null;
            if (className.Equals("com.liferay.portal.model.User"))
            {
                service = new UserService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Address"))
            {
                service = new AddressService(_session);
            }
            else if (className.Equals("com.liferay.portlet.asset.model.AssetCategory"))
            {
                service = new AssetCategoryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.asset.model.AssetEntry"))
            {
                service = new AssetEntryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.asset.model.AssetTag"))
            {
                service = new AssetTagService(_session);
            }
            else if (className.Equals("com.liferay.portlet.asset.model.AssetVocabulary"))
            {
                service = new AssetVocabularyService(_session);
            }
            else if (className.Equals("com.liferay.portlet.blogs.model.BlogsEntry"))
            {
                service = new BlogsEntryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.bookmarks.model.BookmarksEntry"))
            {
                service = new BookmarksEntryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.bookmarks.model.BookmarksFolder"))
            {
                service = new BookmarksFolderService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Company"))
            {
                service = new CompanyService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Contact"))
            {
                service = new ContactService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Country"))
            {
                service = new CountryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.dynamicdatalists.model.DDLRecord"))
            {
                service = new DDLRecordService(_session);
            }
            else if (className.Equals("com.liferay.portlet.dynamicdatalists.model.DDLRecordSet"))
            {
                service = new DDLRecordSetService(_session);
            }
            else if (className.Equals("com.liferay.portlet.dynamicdatamapping.model.DDMStructure"))
            {
                service = new DDMStructureService(_session);
            }
            else if (className.Equals("com.liferay.portlet.dynamicdatamapping.model.DDMTemplate"))
            {
                service = new DDMTemplateService(_session);
            }
            else if (className.Equals("com.liferay.portlet.documentlibrary.model.DLFileEntry"))
            {
                service = new DLFileEntryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.documentlibrary.model.DLFileEntryType"))
            {
                service = new DLFileEntryTypeService(_session);
            }
            else if (className.Equals("com.liferay.portlet.documentlibrary.model.DLFileVersion"))
            {
                service = new DLFileVersionService(_session);
            }
            else if (className.Equals("com.liferay.portlet.documentlibrary.model.DLFolder"))
            {
                service = new DLFolderService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.EmailAddress"))
            {
                service = new EmailAddressService(_session);
            }
            else if (className.Equals("com.liferay.portlet.expando.model.ExpandoColumn"))
            {
                service = new ExpandoColumnService(_session);
            }
            else if (className.Equals("com.liferay.portlet.expando.model.ExpandoValue"))
            {
                service = new ExpandoValueService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Group"))
            {
                service = new GroupService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Image"))
            {
                service = new ImageService(_session);
            }
            else if (className.Equals("com.liferay.portlet.journal.model.JournalArticle"))
            {
                service = new JournalArticleService(_session);
            }
            else if (className.Equals("com.liferay.portlet.journal.model.JournalFeed"))
            {
                service = new JournalFeedService(_session);
            }
            else if (className.Equals("com.liferay.portlet.journal.model.JournalFolder"))
            {
                service = new JournalFolderService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Layout"))
            {
                service = new LayoutService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.LayoutBranch"))
            {
                service = new LayoutBranchService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.LayoutPrototype"))
            {
                service = new LayoutPrototypeService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.LayoutRevision"))
            {
                service = new LayoutRevisionService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.LayoutSet"))
            {
                service = new LayoutSetService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.LayoutSetPrototype"))
            {
                service = new LayoutSetPrototypeService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.ListType"))
            {
                service = new ListTypeService(_session);
            }
            else if (className.Equals("com.liferay.portlet.messageboards.model.MBBan"))
            {
                service = new MBBanService(_session);
            }
            else if (className.Equals("com.liferay.portlet.messageboards.model.MBCategory"))
            {
                service = new MBCategoryService(_session);
            }
            else if (className.Equals("com.liferay.portlet.messageboards.model.MBMessage"))
            {
                service = new MBMessageService(_session);
            }
            else if (className.Equals("com.liferay.portlet.messageboards.model.MBThread"))
            {
                service = new MBThreadService(_session);
            }
            else if (className.Equals("com.liferay.portlet.mobiledevicerules.model.MDRAction"))
            {
                service = new MDRActionService(_session);
            }
            else if (className.Equals("com.liferay.portlet.mobiledevicerules.model.MDRRule"))
            {
                service = new MDRRuleService(_session);
            }
            else if (className.Equals("com.liferay.portlet.mobiledevicerules.model.MDRRuleGroup"))
            {
                service = new MDRRuleGroupService(_session);
            }
            else if (className.Equals("com.liferay.portlet.mobiledevicerules.model.MDRRuleGroupInstance"))
            {
                service = new MDRRuleGroupInstanceService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.MembershipRequest"))
            {
                service = new MembershipRequestService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Organization"))
            {
                service = new OrganizationService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.OrgLabor"))
            {
                service = new OrgLaborService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.PasswordPolicy"))
            {
                service = new PasswordPolicyService(_session);
            }
            else if (className.Equals("Permission"))
            {
                service = new PermissionService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Phone"))
            {
                service = new PhoneService(_session);
            }
            else if (className.Equals("Portal"))
            {
                service = new PortalService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Portlet"))
            {
                service = new PortletService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.PortletPreferences"))
            {
                service = new PortletPreferencesService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Repository"))
            {
                service = new RepositoryService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.ResourcePermission"))
            {
                service = new ResourcePermissionService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Role"))
            {
                service = new RoleService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.Team"))
            {
                service = new TeamService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.UserGroup"))
            {
                service = new UserGroupService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.UserGroupGroupRole"))
            {
                service = new UserGroupGroupRoleService(_session);
            }
            else if (className.Equals("com.liferay.portal.model.UserGroupRole"))
            {
                service = new UserGroupRoleService(_session);
            }
            else if (className.Equals("com.liferay.portlet.wiki.model.WikiNode"))
            {
                service = new WikiNodeService(_session);
            }
            else if (className.Equals("com.liferay.portlet.wiki.model.WikiPage"))
            {
                service = new WikiPageService(_session);
            }

            return service;
        }



    }
}
