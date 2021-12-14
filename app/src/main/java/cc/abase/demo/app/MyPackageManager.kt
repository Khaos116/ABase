package cc.abase.demo.app

import android.content.*
import android.content.pm.*
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES
import android.os.UserHandle
import androidx.annotation.RequiresApi

/**
 * Description:
 * @author: Khaos
 * @date: 2020/7/24 18:12
 */
@Suppress("DEPRECATION")
class MyPackageManager(private val originManager: PackageManager) : PackageManager() {
    override fun getLaunchIntentForPackage(p0: String): Intent? = originManager.getLaunchIntentForPackage(p0)

    override fun getResourcesForApplication(p0: ApplicationInfo): Resources = originManager.getResourcesForApplication(p0)

    override fun getResourcesForApplication(p0: String): Resources = originManager.getResourcesForApplication(p0)

    override fun getReceiverInfo(p0: ComponentName, p1: Int): ActivityInfo = originManager.getReceiverInfo(p0, p1)

    override fun queryIntentActivityOptions(
        p0: ComponentName?,
        p1: Array<out Intent>?,
        p2: Intent,
        p3: Int
    ): MutableList<ResolveInfo> = originManager.queryIntentActivityOptions(p0, p1, p2, p3)

    override fun getApplicationIcon(p0: ApplicationInfo): Drawable = originManager.getApplicationIcon(p0)

    override fun getApplicationIcon(p0: String): Drawable = originManager.getApplicationIcon(p0)

    override fun extendVerificationTimeout(p0: Int, p1: Int, p2: Long) = originManager.extendVerificationTimeout(p0, p1, p2)

    override fun getApplicationEnabledSetting(p0: String): Int = originManager.getApplicationEnabledSetting(p0)

    override fun queryIntentServices(p0: Intent, p1: Int): MutableList<ResolveInfo> = originManager.queryIntentServices(p0, p1)

    override fun isPermissionRevokedByPolicy(p0: String, p1: String): Boolean = originManager.isPermissionRevokedByPolicy(p0, p1)

    override fun checkPermission(p0: String, p1: String): Int = originManager.checkPermission(p0, p1)

    override fun checkSignatures(p0: String, p1: String): Int = originManager.checkSignatures(p0, p1)

    override fun checkSignatures(p0: Int, p1: Int): Int = originManager.checkSignatures(p0, p1)

    override fun removePackageFromPreferred(p0: String) = originManager.removePackageFromPreferred(p0)

    override fun addPermission(p0: PermissionInfo): Boolean = originManager.addPermission(p0)

    override fun getDrawable(p0: String, p1: Int, p2: ApplicationInfo?): Drawable? = originManager.getDrawable(p0, p1, p2)

    @RequiresApi(VERSION_CODES.O)
    override fun getChangedPackages(p0: Int): ChangedPackages? = originManager.getChangedPackages(p0)

    override fun getPackageInfo(p0: String, p1: Int): PackageInfo = originManager.getPackageInfo(p0, p1)

    @RequiresApi(VERSION_CODES.O)
    override fun getPackageInfo(p0: VersionedPackage, p1: Int): PackageInfo = originManager.getPackageInfo(p0, p1)

    override fun getPackagesHoldingPermissions(p0: Array<String>, p1: Int): MutableList<PackageInfo> =
        originManager.getPackagesHoldingPermissions(p0, p1)

    override fun addPermissionAsync(p0: PermissionInfo): Boolean = originManager.addPermissionAsync(p0)

    override fun getSystemAvailableFeatures(): Array<FeatureInfo> = originManager.systemAvailableFeatures

    override fun getSystemSharedLibraryNames(): Array<String>? = originManager.systemSharedLibraryNames

    override fun queryIntentContentProviders(p0: Intent, p1: Int): MutableList<ResolveInfo> =
        originManager.queryIntentContentProviders(p0, p1)

    override fun getApplicationBanner(p0: ApplicationInfo): Drawable? = originManager.getApplicationBanner(p0)

    override fun getApplicationBanner(p0: String): Drawable? = originManager.getApplicationBanner(p0)

    override fun getPackageGids(p0: String): IntArray = originManager.getPackageGids(p0)

    @RequiresApi(VERSION_CODES.N)
    override fun getPackageGids(p0: String, p1: Int): IntArray = originManager.getPackageGids(p0, p1)

    override fun getResourcesForActivity(p0: ComponentName): Resources = originManager.getResourcesForActivity(p0)

    override fun getPackagesForUid(p0: Int): Array<String>? = originManager.getPackagesForUid(p0)

    override fun getPermissionGroupInfo(p0: String, p1: Int): PermissionGroupInfo = originManager.getPermissionGroupInfo(p0, p1)

    override fun addPackageToPreferred(p0: String) = originManager.addPackageToPreferred(p0)

    override fun getComponentEnabledSetting(p0: ComponentName): Int = originManager.getComponentEnabledSetting(p0)

    override fun getLeanbackLaunchIntentForPackage(p0: String): Intent? = originManager.getLeanbackLaunchIntentForPackage(p0)

    override fun getInstalledPackages(p0: Int): MutableList<PackageInfo> = originManager.getInstalledPackages(p0)

    override fun getAllPermissionGroups(p0: Int): MutableList<PermissionGroupInfo> = originManager.getAllPermissionGroups(p0)

    override fun getNameForUid(p0: Int): String? = originManager.getNameForUid(p0)

    @RequiresApi(VERSION_CODES.O)
    override fun updateInstantAppCookie(p0: ByteArray?) = originManager.updateInstantAppCookie(p0)

    override fun getApplicationLogo(p0: ApplicationInfo): Drawable? = originManager.getApplicationLogo(p0)

    override fun getApplicationLogo(p0: String): Drawable? = originManager.getApplicationLogo(p0)

    override fun getApplicationLabel(p0: ApplicationInfo): CharSequence = originManager.getApplicationLabel(p0)

    override fun getPreferredActivities(p0: MutableList<IntentFilter>, p1: MutableList<ComponentName>, p2: String?): Int =
        originManager.getPreferredActivities(p0, p1, p2)

    @RequiresApi(VERSION_CODES.O)
    override fun setApplicationCategoryHint(p0: String, p1: Int) = originManager.setApplicationCategoryHint(p0, p1)

    override fun setInstallerPackageName(p0: String, p1: String?) = originManager.setInstallerPackageName(p0, p1)

    override fun getUserBadgedLabel(p0: CharSequence, p1: UserHandle): CharSequence = originManager.getUserBadgedLabel(p0, p1)

    @RequiresApi(VERSION_CODES.O)
    override fun canRequestPackageInstalls(): Boolean = originManager.canRequestPackageInstalls()

    @RequiresApi(VERSION_CODES.O)
    override fun isInstantApp(): Boolean = originManager.isInstantApp

    @RequiresApi(VERSION_CODES.O)
    override fun isInstantApp(p0: String): Boolean = originManager.isInstantApp(p0)

    override fun getActivityIcon(p0: ComponentName): Drawable = originManager.getActivityIcon(p0)

    override fun getActivityIcon(p0: Intent): Drawable = originManager.getActivityIcon(p0)

    override fun canonicalToCurrentPackageNames(p0: Array<String>): Array<String> = originManager.canonicalToCurrentPackageNames(p0)

    override fun getProviderInfo(p0: ComponentName, p1: Int): ProviderInfo = originManager.getProviderInfo(p0, p1)

    override fun clearPackagePreferredActivities(p0: String) = originManager.clearPackagePreferredActivities(p0)

    override fun getPackageInstaller(): PackageInstaller = originManager.packageInstaller

    override fun resolveService(p0: Intent, p1: Int): ResolveInfo? = originManager.resolveService(p0, p1)

    override fun verifyPendingInstall(p0: Int, p1: Int) = originManager.verifyPendingInstall(p0, p1)

    @RequiresApi(VERSION_CODES.O)
    override fun getInstantAppCookie(): ByteArray = originManager.instantAppCookie

    override fun getText(p0: String, p1: Int, p2: ApplicationInfo?): CharSequence? = originManager.getText(p0, p1, p2)

    override fun resolveContentProvider(p0: String, p1: Int): ProviderInfo? = originManager.resolveContentProvider(p0, p1)

    override fun hasSystemFeature(p0: String): Boolean = originManager.hasSystemFeature(p0)

    @RequiresApi(VERSION_CODES.N)
    override fun hasSystemFeature(p0: String, p1: Int): Boolean = originManager.hasSystemFeature(p0, p1)

    override fun getInstrumentationInfo(p0: ComponentName, p1: Int): InstrumentationInfo =
        originManager.getInstrumentationInfo(p0, p1)

    override fun getInstalledApplications(p0: Int): MutableList<ApplicationInfo> = originManager.getInstalledApplications(p0)

    override fun getUserBadgedDrawableForDensity(p0: Drawable, p1: UserHandle, p2: Rect?, p3: Int): Drawable =
        originManager.getUserBadgedDrawableForDensity(p0, p1, p2, p3)

    @RequiresApi(VERSION_CODES.O)
    override fun getInstantAppCookieMaxBytes(): Int = originManager.instantAppCookieMaxBytes

    override fun getDefaultActivityIcon(): Drawable = originManager.defaultActivityIcon

    override fun getPreferredPackages(p0: Int): MutableList<PackageInfo> = originManager.getPreferredPackages(p0)

    override fun addPreferredActivity(p0: IntentFilter, p1: Int, p2: Array<ComponentName>?, p3: ComponentName) =
        originManager.addPreferredActivity(p0, p1, p2, p3)

    @RequiresApi(VERSION_CODES.O)
    override fun getSharedLibraries(p0: Int): MutableList<SharedLibraryInfo> = originManager.getSharedLibraries(p0)

    override fun queryIntentActivities(p0: Intent, p1: Int): MutableList<ResolveInfo> = originManager.queryIntentActivities(p0, p1)

    override fun getActivityBanner(p0: ComponentName): Drawable? = originManager.getActivityBanner(p0)

    override fun getActivityBanner(p0: Intent): Drawable? = originManager.getActivityBanner(p0)

    override fun setComponentEnabledSetting(p0: ComponentName, p1: Int, p2: Int) =
        originManager.setComponentEnabledSetting(p0, p1, p2)

    override fun getApplicationInfo(p0: String, p1: Int): ApplicationInfo = originManager.getApplicationInfo(p0, p1)

    override fun resolveActivity(p0: Intent, p1: Int): ResolveInfo? = originManager.resolveActivity(p0, p1)

    override fun queryBroadcastReceivers(p0: Intent, p1: Int): MutableList<ResolveInfo> =
        originManager.queryBroadcastReceivers(p0, p1)

    override fun getXml(p0: String, p1: Int, p2: ApplicationInfo?): XmlResourceParser? = originManager.getXml(p0, p1, p2)

    override fun getActivityLogo(p0: ComponentName): Drawable? = originManager.getActivityLogo(p0)

    override fun getActivityLogo(p0: Intent): Drawable? = originManager.getActivityLogo(p0)

    override fun queryContentProviders(p0: String?, p1: Int, p2: Int): MutableList<ProviderInfo> =
        originManager.queryContentProviders(p0, p1, p2)

    override fun getPermissionInfo(p0: String, p1: Int): PermissionInfo = originManager.getPermissionInfo(p0, p1)

    override fun queryPermissionsByGroup(permissionGroup: String?, flags: Int): MutableList<PermissionInfo> = originManager.queryPermissionsByGroup(permissionGroup, flags)

    override fun removePermission(p0: String) = originManager.removePermission(p0)

    override fun queryInstrumentation(p0: String, p1: Int): MutableList<InstrumentationInfo> =
        originManager.queryInstrumentation(p0, p1)

    @RequiresApi(VERSION_CODES.O)
    override fun clearInstantAppCookie() = originManager.clearInstantAppCookie()

    override fun currentToCanonicalPackageNames(p0: Array<String>): Array<String> = originManager.currentToCanonicalPackageNames(p0)

    @RequiresApi(VERSION_CODES.N)
    override fun getPackageUid(p0: String, p1: Int): Int = originManager.getPackageUid(p0, p1)

    override fun getUserBadgedIcon(p0: Drawable, p1: UserHandle): Drawable = originManager.getUserBadgedIcon(p0, p1)

    override fun getActivityInfo(p0: ComponentName, p1: Int): ActivityInfo = originManager.getActivityInfo(p0, p1)

    override fun isSafeMode(): Boolean = originManager.isSafeMode

    override fun getInstallerPackageName(p0: String): String? = originManager.getInstallerPackageName(p0)

    override fun setApplicationEnabledSetting(p0: String, p1: Int, p2: Int) = originManager.setApplicationEnabledSetting(p0, p1, p2)

    override fun getServiceInfo(p0: ComponentName, p1: Int): ServiceInfo = originManager.getServiceInfo(p0, p1)
}