//
//  LiferayPlugin.h
//  PhonegapLiferay
//
//  Created by Salvador Tejero Silva on 15/1/15.
//
//

#import <Cordova/CDVPlugin.h>
#import <objc/runtime.h>
#import "LRSession.h"
#import "LRUserService_v62.h"
#import "LRCallback.h"
#import "LRBaseService.h"
#import "LRAddressService_v62.h"
#import "LRAssetCategoryService_v62.h"
#import "LRWikiPageService_v62.h"
#import "LROrganizationService_v62.h"
#import "LROrgLaborService_v62.h"
#import "LRPasswordPolicyService_v62.h"
#import "LRPermissionService_v62.h"
#import "LRPhoneService_v62.h"
#import "LRPortalService_v62.h"
#import "LRPortletPreferencesService_v62.h"
#import "LRRepositoryService_v62.h"
#import "LRResourcePermissionService_v62.h"
#import "LRRoleService_v62.h"
#import "LRTeamService_v62.h"
#import "LRUserGroupService_v62.h"
#import "LRUserGroupGroupRoleService_v62.h"
#import "LRUserGroupRoleService_v62.h"
#import "LRWikiNodeService_v62.h"
#import "LRAssetEntryService_v62.h"
#import "LRAssetTagService_v62.h"
#import "LRAssetVocabularyService_v62.h"
#import "LRBlogsEntryService_v62.h"
#import "LRBookmarksEntryService_v62.h"
#import "LRBookmarksFolderService_v62.h"
#import "LRCompanyService_v62.h"
#import "LRContactService_v62.h"
#import "LRCountryService_v62.h"
#import "LRDDLRecordService_v62.h"
#import "LRDDLRecordSetService_v62.h"
#import "LRDDMStructureService_v62.h"
#import "LRDDMTemplateService_v62.h"
#import "LRDLFileEntryService_v62.h"
#import "LRDLFileEntryTypeService_v62.h"
#import "LRDLFileVersionService_v62.h"
#import "LRDLFolderService_v62.h"
#import "LREmailAddressService_v62.h"
#import "LRExpandoColumnService_v62.h"
#import "LRExpandoValueService_v62.h"
#import "LRGroupService_v62.h"
#import "LRImageService_v62.h"
#import "LRJournalArticleService_v62.h"
#import "LRJournalFeedService_v62.h"
#import "LRJournalFolderService_v62.h"
#import "LRLayoutService_v62.h"
#import "LRLayoutBranchService_v62.h"
#import "LRLayoutPrototypeService_v62.h"
#import "LRLayoutRevisionService_v62.h"
#import "LRLayoutSetService_v62.h"
#import "LRLayoutSetPrototypeService_v62.h"
#import "LRListTypeService_v62.h"
#import "LRMBBanService_v62.h"
#import "LRMBCategoryService_v62.h"
#import "LRMBMessageService_v62.h"
#import "LRMBThreadService_v62.h"
#import "LRMDRActionService_v62.h"
#import "LRMDRRuleService_v62.h"
#import "LRMDRRuleGroupService_v62.h"
#import "LRMDRRuleGroupInstanceService_v62.h"
#import "LRMembershipRequestService_v62.h"
#import "LROrganizationService_v62.h"
#import "LROrgLaborService_v62.h"
#import "LRPasswordPolicyService_v62.h"
#import "LRPermissionService_v62.h"
#import "LRPhoneService_v62.h"
#import "LRPortletService_v62.h"
#import "LRPortletPreferencesService_v62.h"
#import "LRRepositoryService_v62.h"
#import "LRResourcePermissionService_v62.h"
#import "LRUserGroupService_v62.h"
#import "LRUserGroupGroupRoleService_v62.h"

@interface LiferayPlugin :  CDVPlugin{
}

@property (copy) NSString* callbackId;


- (void)connect:(CDVInvokedUrlCommand*)command;


- (void)execute:(CDVInvokedUrlCommand*)command;


@end