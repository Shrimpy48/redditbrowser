package com.example.redditbrowser

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthResponse {

    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null
    @SerializedName("expires_in")
    @Expose
    var expiresIn: Int? = null
    @SerializedName("scope")
    @Expose
    var scope: String? = null
    @SerializedName("token_type")
    @Expose
    var tokenType: String? = null

}

class SelfInfo {

    @SerializedName("comment_karma")
    @Expose
    var commentKarma: Int? = null
    @SerializedName("created")
    @Expose
    var created: Double? = null
    @SerializedName("created_utc")
    @Expose
    var createdUtc: Double? = null
    @SerializedName("has_mail")
    @Expose
    var hasMail: Boolean? = null
    @SerializedName("has_mod_mail")
    @Expose
    var hasModMail: Boolean? = null
    @SerializedName("has_verified_email")
    @Expose
    var hasVerifiedEmail: Any? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("is_gold")
    @Expose
    var isGold: Boolean? = null
    @SerializedName("is_mod")
    @Expose
    var isMod: Boolean? = null
    @SerializedName("link_karma")
    @Expose
    var linkKarma: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("over_18")
    @Expose
    var over18: Boolean? = null

}

class SubredditInfoWrapper {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: SubredditInfo? = null

}

class SubredditInfoList {

    @SerializedName("modhash")
    @Expose
    var modhash: String? = null
    @SerializedName("dist")
    @Expose
    var dist: Int? = null
    @SerializedName("children")
    @Expose
    var children: List<SubredditInfoWrapper>? = null
    @SerializedName("after")
    @Expose
    var after: String? = null
    @SerializedName("before")
    @Expose
    var before: String? = null

}

class SubredditInfo {

    @SerializedName("user_flair_background_color")
    @Expose
    var userFlairBackgroundColor: String? = null
    @SerializedName("submit_text_html")
    @Expose
    var submitTextHtml: String? = null
    @SerializedName("restrict_posting")
    @Expose
    var restrictPosting: Boolean? = null
    @SerializedName("user_is_banned")
    @Expose
    var userIsBanned: Boolean? = null
    @SerializedName("free_form_reports")
    @Expose
    var freeFormReports: Boolean? = null
    @SerializedName("wiki_enabled")
    @Expose
    var wikiEnabled: Boolean? = null
    @SerializedName("user_is_muted")
    @Expose
    var userIsMuted: Boolean? = null
    @SerializedName("user_can_flair_in_sr")
    @Expose
    var userCanFlairInSr: String? = null
    @SerializedName("display_name")
    @Expose
    var displayName: String? = null
    @SerializedName("header_img")
    @Expose
    var headerImg: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("icon_size")
    @Expose
    var iconSize: List<Int>? = null
    @SerializedName("primary_color")
    @Expose
    var primaryColor: String? = null
    @SerializedName("active_user_count")
    @Expose
    var activeUserCount: String? = null
    @SerializedName("icon_img")
    @Expose
    var iconImg: String? = null
    @SerializedName("display_name_prefixed")
    @Expose
    var displayNamePrefixed: String? = null
    @SerializedName("accounts_active")
    @Expose
    var accountsActive: String? = null
    @SerializedName("public_traffic")
    @Expose
    var publicTraffic: Boolean? = null
    @SerializedName("subscribers")
    @Expose
    var subscribers: Int? = null
    @SerializedName("user_flair_richtext")
    @Expose
    var userFlairRichtext: List<Any>? = null
    @SerializedName("videostream_links_count")
    @Expose
    var videostreamLinksCount: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("quarantine")
    @Expose
    var quarantine: Boolean? = null
    @SerializedName("hide_ads")
    @Expose
    var hideAds: Boolean? = null
    @SerializedName("emojis_enabled")
    @Expose
    var emojisEnabled: Boolean? = null
    @SerializedName("advertiser_category")
    @Expose
    var advertiserCategory: String? = null
    @SerializedName("public_description")
    @Expose
    var publicDescription: String? = null
    @SerializedName("comment_score_hide_mins")
    @Expose
    var commentScoreHideMins: Int? = null
    @SerializedName("user_has_favorited")
    @Expose
    var userHasFavorited: Boolean? = null
    @SerializedName("user_flair_template_id")
    @Expose
    var userFlairTemplateId: String? = null
    @SerializedName("community_icon")
    @Expose
    var communityIcon: String? = null
    @SerializedName("banner_background_image")
    @Expose
    var bannerBackgroundImage: String? = null
    @SerializedName("original_content_tag_enabled")
    @Expose
    var originalContentTagEnabled: Boolean? = null
    @SerializedName("submit_text")
    @Expose
    var submitText: String? = null
    @SerializedName("description_html")
    @Expose
    var descriptionHtml: String? = null
    @SerializedName("spoilers_enabled")
    @Expose
    var spoilersEnabled: Boolean? = null
    @SerializedName("header_title")
    @Expose
    var headerTitle: String? = null
    @SerializedName("header_size")
    @Expose
    var headerSize: List<Int>? = null
    @SerializedName("user_flair_position")
    @Expose
    var userFlairPosition: String? = null
    @SerializedName("all_original_content")
    @Expose
    var allOriginalContent: Boolean? = null
    @SerializedName("has_menu_widget")
    @Expose
    var hasMenuWidget: Boolean? = null
    @SerializedName("is_enrolled_in_new_modmail")
    @Expose
    var isEnrolledInNewModmail: String? = null
    @SerializedName("key_color")
    @Expose
    var keyColor: String? = null
    @SerializedName("can_assign_user_flair")
    @Expose
    var canAssignUserFlair: Boolean? = null
    @SerializedName("created")
    @Expose
    var created: Double? = null
    @SerializedName("wls")
    @Expose
    var wls: Int? = null
    @SerializedName("show_media_preview")
    @Expose
    var showMediaPreview: Boolean? = null
    @SerializedName("submission_type")
    @Expose
    var submissionType: String? = null
    @SerializedName("user_is_subscriber")
    @Expose
    var userIsSubscriber: Boolean? = null
    @SerializedName("disable_contributor_requests")
    @Expose
    var disableContributorRequests: Boolean? = null
    @SerializedName("allow_videogifs")
    @Expose
    var allowVideogifs: Boolean? = null
    @SerializedName("user_flair_type")
    @Expose
    var userFlairType: String? = null
    @SerializedName("collapse_deleted_comments")
    @Expose
    var collapseDeletedComments: Boolean? = null
    @SerializedName("emojis_custom_size")
    @Expose
    var emojisCustomSize: String? = null
    @SerializedName("public_description_html")
    @Expose
    var publicDescriptionHtml: String? = null
    @SerializedName("allow_videos")
    @Expose
    var allowVideos: Boolean? = null
    @SerializedName("notification_level")
    @Expose
    var notificationLevel: String? = null
    @SerializedName("can_assign_link_flair")
    @Expose
    var canAssignLinkFlair: Boolean? = null
    @SerializedName("accounts_active_is_fuzzed")
    @Expose
    var accountsActiveIsFuzzed: Boolean? = null
    @SerializedName("submit_text_label")
    @Expose
    var submitTextLabel: String? = null
    @SerializedName("link_flair_position")
    @Expose
    var linkFlairPosition: String? = null
    @SerializedName("user_sr_flair_enabled")
    @Expose
    var userSrFlairEnabled: String? = null
    @SerializedName("user_flair_enabled_in_sr")
    @Expose
    var userFlairEnabledInSr: Boolean? = null
    @SerializedName("allow_discovery")
    @Expose
    var allowDiscovery: Boolean? = null
    @SerializedName("user_sr_theme_enabled")
    @Expose
    var userSrThemeEnabled: Boolean? = null
    @SerializedName("link_flair_enabled")
    @Expose
    var linkFlairEnabled: Boolean? = null
    @SerializedName("subreddit_type")
    @Expose
    var subredditType: String? = null
    @SerializedName("suggested_comment_sort")
    @Expose
    var suggestedCommentSort: String? = null
    @SerializedName("banner_img")
    @Expose
    var bannerImg: String? = null
    @SerializedName("user_flair_text")
    @Expose
    var userFlairText: String? = null
    @SerializedName("banner_background_color")
    @Expose
    var bannerBackgroundColor: String? = null
    @SerializedName("show_media")
    @Expose
    var showMedia: Boolean? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("user_is_moderator")
    @Expose
    var userIsModerator: Boolean? = null
    @SerializedName("over18")
    @Expose
    var over18: Boolean? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("submit_link_label")
    @Expose
    var submitLinkLabel: String? = null
    @SerializedName("user_flair_text_color")
    @Expose
    var userFlairTextColor: String? = null
    @SerializedName("restrict_commenting")
    @Expose
    var restrictCommenting: Boolean? = null
    @SerializedName("user_flair_css_class")
    @Expose
    var userFlairCssClass: String? = null
    @SerializedName("allow_images")
    @Expose
    var allowImages: Boolean? = null
    @SerializedName("lang")
    @Expose
    var lang: String? = null
    @SerializedName("whitelist_status")
    @Expose
    var whitelistStatus: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("created_utc")
    @Expose
    var createdUtc: Double? = null
    @SerializedName("banner_size")
    @Expose
    var bannerSize: List<Int>? = null
    @SerializedName("mobile_banner_image")
    @Expose
    var mobileBannerImage: String? = null
    @SerializedName("user_is_contributor")
    @Expose
    var userIsContributor: Boolean? = null

}

class SubredditInfoListWrapper {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: SubredditInfoList? = null

}
