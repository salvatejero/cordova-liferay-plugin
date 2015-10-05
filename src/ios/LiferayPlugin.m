//
//  LiferayPlugin.m
//  PhonegapLiferay
//
//  Created by Salvador Tejero Silva on 15/1/15.
//
//

#import "LiferayPlugin.h"
#import "LRBasicAuthentication.h"
#import "LRCallback.h"

@implementation LiferayPlugin

@synthesize session, callbackId;

- (void)connect:(CDVInvokedUrlCommand*)command
{
    NSArray *params = command.arguments;
    callbackId = command.callbackId;
    session = [[LRSession alloc] initWithServer:params[0]
                                            authentication:[[LRBasicAuthentication alloc] initWithUsername:params[1] password:params[2]]];
    
    [self getUser];
    
}

- (void)execute:(CDVInvokedUrlCommand*)command
{
    
    callbackId = command.callbackId;
    NSArray *params = command.arguments;
    [session setCallback: self];
    [self objectModelWithClassName:params[0] withMethodName:params[1] withParams:params[2]];
}

-(void) objectModelWithClassName:(NSString*)className withMethodName:(NSString*) methodName withParams: (NSArray*) jsonArray
{
    LRBaseService *service = [self serviceWithClassName:className];
    
    unsigned int methodCount = 0;
    Method *methods = class_copyMethodList([service class], &methodCount);
    
    Method *methodToExecute = nil;
    NSString *methodNameString = nil;
    UInt32 numberParams = 0;
    for (unsigned int i = 0; i < methodCount; i++) {
        Method method = methods[i];
        methodNameString = [NSString stringWithCString: sel_getName(method_getName(method))
                                                        encoding: NSASCIIStringEncoding];
        
        NSString * methodName2 = [methodNameString stringByReplacingOccurrencesOfString: @ "Async" withString: @ ""];
        if([methodNameString hasPrefix:methodName] || [[methodName2 lowercaseString] hasPrefix:methodName])
        {
            numberParams = method_getNumberOfArguments(method) - 2;  // Count only the method's parameters
            if(numberParams == [jsonArray count] + 1){
                
                methodToExecute = &method;
                break;
            }else{
               NSLog(@"the method '%@' is similar but params is incorrect, is it the method that you are looking for? ",methodNameString); 
            }
            
            
        }
        
    }
    free(methods);
    if(methodToExecute != nil){
        
        NSLog(@"%s", method_getTypeEncoding(*methodToExecute));

        SEL sel = NSSelectorFromString(methodNameString);
        NSMethodSignature *sig = [[service class] instanceMethodSignatureForSelector:sel];
        NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:sig];
        [invocation setSelector:sel];
        [invocation setTarget:service];
        int i = 2;
        for(int j=0; j< [jsonArray count]; j++){
            NSString* arg1 = jsonArray[j];
            
            const char* argType = [[invocation methodSignature] getArgumentTypeAtIndex:i];
            
            if(!strcmp(argType, @encode(id))) {
                [invocation setArgument:&arg1 atIndex:i];
            } else if(!strcmp(argType, @encode(int))) {
                int arg = [arg1 intValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(bool))) {
                bool arg = [arg1 boolValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(NSString))) {
                [invocation setArgument:&arg1 atIndex:i];
            } else if(!strcmp(argType, @encode(BOOL))) {
                BOOL arg = [arg1 boolValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(short))) {
                short arg = [arg1 intValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(float))) {
                float arg = [arg1 floatValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(double))) {
                double arg = [arg1 doubleValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(long))) {
                long arg = [arg1 longLongValue];
                [invocation setArgument:&arg atIndex:i];
            } else if(!strcmp(argType, @encode(long long))) {
                long long arg = [arg1 longLongValue];
                [invocation setArgument:&arg atIndex:i];
            } else {
                NSAssert1(NO, @"-- Unhandled type: %s", argType);
            }
            i ++;
        }
        
        __autoreleasing NSError *error;
        __autoreleasing NSError **errorPointer = &error;
        [invocation setArgument:&errorPointer atIndex:i];
        [invocation invoke];
        __strong NSError *getError = *errorPointer;
        NSLog(@"%@", getError);
    }
}



-(LRBaseService*) serviceWithClassName:(NSString*) className
{
    LRBaseService *service = nil;
    if ([className isEqualToString:@"com.liferay.portal.model.User"]) {
        service = [[LRUserService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Address"]) {
        service = [[LRAddressService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.asset.model.AssetCategory"]) {
        service = [[LRAssetCategoryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.asset.model.AssetEntry"]) {
        service = [[LRAssetEntryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.asset.model.AssetTag"]) {
        service = [[LRAssetTagService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.asset.model.AssetVocabulary"]) {
         service = [[LRAssetVocabularyService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.blogs.model.BlogsEntry"]) {
        service = [[LRBlogsEntryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.bookmarks.model.BookmarksEntry"]) {
        service = [[LRBookmarksEntryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.bookmarks.model.BookmarksFolder"]) {
        service = [[LRBookmarksFolderService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Company"]) {
        service = [[LRCompanyService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Contact"]) {
        service = [[LRContactService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Country"]) {
        service = [[LRCountryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.dynamicdatalists.model.DDLRecord"]) {
        service = [[LRDDLRecordService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.dynamicdatalists.model.DDLRecordSet"]) {
        service = [[LRDDLRecordSetService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.dynamicdatamapping.model.DDMStructure"]) {
        service = [[LRDDMStructureService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.dynamicdatamapping.model.DDMTemplate"]) {
        service = [[LRDDMTemplateService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.documentlibrary.model.DLFileEntry"]) {
        service = [[LRDLFileEntryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.documentlibrary.model.DLFileEntryType"]) {
        service = [[LRDLFileEntryTypeService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.documentlibrary.model.DLFileVersion"]) {
        service = [[LRDLFileVersionService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.documentlibrary.model.DLFolder"]) {
        service = [[LRDLFolderService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.EmailAddress"]) {
        service = [[LREmailAddressService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.expando.model.ExpandoColumn"]) {
        service = [[LRExpandoColumnService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.expando.model.ExpandoValue"]) {
        service = [[LRExpandoValueService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Group"]) {
        service = [[LRGroupService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Image"]) {
        service = [[LRImageService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.journal.model.JournalArticle"]) {
        service = [[LRJournalArticleService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.journal.model.JournalFeed"]) {
        service = [[LRJournalFeedService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.journal.model.JournalFolder"]) {
        service = [[LRJournalFolderService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Layout"]) {
        service = [[LRLayoutService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.LayoutBranch"]) {
        service = [[LRLayoutBranchService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.LayoutPrototype"]) {
        service = [[LRLayoutPrototypeService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.LayoutRevision"]) {
        service = [[LRLayoutRevisionService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.LayoutSet"]) {
        service = [[LRLayoutSetService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.LayoutSetPrototype"]) {
        service = [[LRLayoutSetPrototypeService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.ListType"]) {
        service = [[LRListTypeService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.messageboards.model.MBBan"]) {
        service = [[LRMBBanService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.messageboards.model.MBCategory"]) {
        service = [[LRMBCategoryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.messageboards.model.MBMessage"]) {
        service = [[LRMBMessageService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.messageboards.model.MBThread"]) {
        service = [[LRMBThreadService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.mobiledevicerules.model.MDRAction"]) {
        service = [[LRMDRActionService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.mobiledevicerules.model.MDRRule"]) {
        service = [[LRMDRRuleService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.mobiledevicerules.model.MDRRuleGroup"]) {
        service = [[LRMDRRuleGroupService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.mobiledevicerules.model.MDRRuleGroupInstance"]) {
        service = [[LRMDRRuleGroupInstanceService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.MembershipRequest"]) {
        service = [[LRMembershipRequestService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Organization"]) {
        service = [[LROrganizationService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.OrgLabor"]) {
       service = [[LROrgLaborService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.PasswordPolicy"]) {
        service = [[LRPasswordPolicyService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"Permission"]) {
        service = [[LRPermissionService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Phone"]) {
        service = [[LRPhoneService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"Portal"]) {
        service = [[LRPortalService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Portlet"]) {
        service = [[LRPortletService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.PortletPreferences"]) {
        service = [[LRPortletPreferencesService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Repository"]) {
        service = [[LRRepositoryService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.ResourcePermission"]) {
        service = [[LRResourcePermissionService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Role"]) {
        service = [[LRRoleService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.Team"]) {
        service = [[LRTeamService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.UserGroup"]) {
        service = [[LRUserGroupService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.UserGroupGroupRole"]) {
        service = [[LRUserGroupGroupRoleService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portal.model.UserGroupRole"]) {
        service = [[LRUserGroupRoleService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.wiki.model.WikiNode"]) {
        service = [[LRWikiNodeService_v62 alloc]initWithSession:session];
    }else  if ([className isEqualToString:@"com.liferay.portlet.wiki.model.WikiPage"]) {
        service = [[LRWikiPageService_v62 alloc]initWithSession:session];
    }
    
    return service;
}

-(void)getUser
{

    NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
    long long defaultCompanyId = [[infoDict objectForKey:@"default-company-id"] longLongValue];
    NSError *error;
    LRUserService_v62 *service = [[LRUserService_v62 alloc] initWithSession:session];
    NSDictionary *user = [service getUserByEmailAddressWithCompanyId:defaultCompanyId emailAddress: session.username error:&error];
    NSLog(@"%@", error);
    [self sendPluginResult:user withErrorMessage: [error localizedDescription]];
}


- (void)sendPluginResult:(NSDictionary*)dict withErrorMessage:(NSString*)errorMessage
{
    
    CDVPluginResult* result = nil;
    
    if(dict == nil)
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
    }else
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
    }
    
    
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}


- (void)onFailure:(NSError *)error {
    // Implement error handling code
    NSLog(@"%@", error);
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

- (void)onSuccess:(id)result {
    // Called after request has finished successfully
    CDVPluginResult* cvResult = nil;
    
    if ([result isKindOfClass:[NSDictionary class]])
    {
        cvResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    }
    else
    {
        cvResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:[result array]];

    }
    [cvResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:cvResult callbackId:callbackId];
    
}

@end

