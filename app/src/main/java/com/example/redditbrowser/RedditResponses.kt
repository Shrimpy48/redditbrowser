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
    @SerializedName("redditId")
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
    var emojisCustomSize: Any? = null
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
    @SerializedName("redditId")
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

class PostInfo {

    @SerializedName("approved_at_utc")
    @Expose
    var approvedAtUtc: String? = null
    @SerializedName("subreddit")
    @Expose
    var subreddit: String? = null
    @SerializedName("selftext")
    @Expose
    var selftext: String? = null
    @SerializedName("author_fullname")
    @Expose
    var authorFullname: String? = null
    @SerializedName("saved")
    @Expose
    var saved: Boolean? = null
    @SerializedName("mod_reason_title")
    @Expose
    var modReasonTitle: String? = null
    @SerializedName("gilded")
    @Expose
    var gilded: Int? = null
    @SerializedName("clicked")
    @Expose
    var clicked: Boolean? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("link_flair_richtext")
    @Expose
    var linkFlairRichtext: List<Richtext>? = null
    @SerializedName("subreddit_name_prefixed")
    @Expose
    var subredditNamePrefixed: String? = null
    @SerializedName("hidden")
    @Expose
    var hidden: Boolean? = null
    @SerializedName("pwls")
    @Expose
    var pwls: Int? = null
    @SerializedName("link_flair_css_class")
    @Expose
    var linkFlairCssClass: String? = null
    @SerializedName("downs")
    @Expose
    var downs: Int? = null
    @SerializedName("thumbnail_height")
    @Expose
    var thumbnailHeight: Int? = null
    @SerializedName("hide_score")
    @Expose
    var hideScore: Boolean? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("quarantine")
    @Expose
    var quarantine: Boolean? = null
    @SerializedName("link_flair_text_color")
    @Expose
    var linkFlairTextColor: String? = null
    @SerializedName("author_flair_background_color")
    @Expose
    var authorFlairBackgroundColor: String? = null
    @SerializedName("subreddit_type")
    @Expose
    var subredditType: String? = null
    @SerializedName("ups")
    @Expose
    var ups: Int? = null
    @SerializedName("total_awards_received")
    @Expose
    var totalAwardsReceived: Int? = null
    @SerializedName("media_embed")
    @Expose
    var mediaEmbed: MediaEmbed? = null
    @SerializedName("thumbnail_width")
    @Expose
    var thumbnailWidth: Int? = null
    @SerializedName("author_flair_template_id")
    @Expose
    var authorFlairTemplateId: String? = null
    @SerializedName("is_original_content")
    @Expose
    var isOriginalContent: Boolean? = null
    @SerializedName("user_reports")
    @Expose
    var userReports: List<Any>? = null
    @SerializedName("secure_media")
    @Expose
    var secureMedia: Media? = null
    @SerializedName("is_reddit_media_domain")
    @Expose
    var isRedditMediaDomain: Boolean? = null
    @SerializedName("is_meta")
    @Expose
    var isMeta: Boolean? = null
    @SerializedName("category")
    @Expose
    var category: String? = null
    @SerializedName("secure_media_embed")
    @Expose
    var secureMediaEmbed: MediaEmbed? = null
    @SerializedName("link_flair_text")
    @Expose
    var linkFlairText: String? = null
    @SerializedName("can_mod_post")
    @Expose
    var canModPost: Boolean? = null
    @SerializedName("score")
    @Expose
    var score: Int? = null
    @SerializedName("approved_by")
    @Expose
    var approvedBy: String? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null
    @SerializedName("edited")
    @Expose
    var edited: Any? = null
    @SerializedName("author_flair_css_class")
    @Expose
    var authorFlairCssClass: String? = null
    @SerializedName("author_flair_richtext")
    @Expose
    var authorFlairRichtext: List<Richtext>? = null
    @SerializedName("gildings")
    @Expose
    var gildings: Gildings? = null
    @SerializedName("post_hint")
    @Expose
    var postHint: String? = null
    @SerializedName("content_categories")
    @Expose
    var contentCategories: Any? = null
    @SerializedName("is_self")
    @Expose
    var isSelf: Boolean? = null
    @SerializedName("mod_note")
    @Expose
    var modNote: String? = null
    @SerializedName("created")
    @Expose
    var created: Double? = null
    @SerializedName("link_flair_type")
    @Expose
    var linkFlairType: String? = null
    @SerializedName("wls")
    @Expose
    var wls: Int? = null
    @SerializedName("banned_by")
    @Expose
    var bannedBy: String? = null
    @SerializedName("author_flair_type")
    @Expose
    var authorFlairType: String? = null
    @SerializedName("domain")
    @Expose
    var domain: String? = null
    @SerializedName("selftext_html")
    @Expose
    var selftextHtml: String? = null
    @SerializedName("likes")
    @Expose
    var likes: String? = null
    @SerializedName("suggested_sort")
    @Expose
    var suggestedSort: String? = null
    @SerializedName("banned_at_utc")
    @Expose
    var bannedAtUtc: String? = null
    @SerializedName("view_count")
    @Expose
    var viewCount: String? = null
    @SerializedName("archived")
    @Expose
    var archived: Boolean? = null
    @SerializedName("no_follow")
    @Expose
    var noFollow: Boolean? = null
    @SerializedName("is_crosspostable")
    @Expose
    var isCrosspostable: Boolean? = null
    @SerializedName("pinned")
    @Expose
    var pinned: Boolean? = null
    @SerializedName("over_18")
    @Expose
    var over18: Boolean? = null
    @SerializedName("preview")
    @Expose
    var preview: Preview? = null
    @SerializedName("all_awardings")
    @Expose
    var allAwardings: List<AllAwarding>? = null
    @SerializedName("media_only")
    @Expose
    var mediaOnly: Boolean? = null
    @SerializedName("link_flair_template_id")
    @Expose
    var linkFlairTemplateId: String? = null
    @SerializedName("can_gild")
    @Expose
    var canGild: Boolean? = null
    @SerializedName("spoiler")
    @Expose
    var spoiler: Boolean? = null
    @SerializedName("locked")
    @Expose
    var locked: Boolean? = null
    @SerializedName("author_flair_text")
    @Expose
    var authorFlairText: String? = null
    @SerializedName("visited")
    @Expose
    var visited: Boolean? = null
    @SerializedName("num_reports")
    @Expose
    var numReports: String? = null
    @SerializedName("distinguished")
    @Expose
    var distinguished: String? = null
    @SerializedName("subreddit_id")
    @Expose
    var subredditId: String? = null
    @SerializedName("mod_reason_by")
    @Expose
    var modReasonBy: String? = null
    @SerializedName("removal_reason")
    @Expose
    var removalReason: String? = null
    @SerializedName("link_flair_background_color")
    @Expose
    var linkFlairBackgroundColor: String? = null
    @SerializedName("redditId")
    @Expose
    var id: String? = null
    @SerializedName("is_robot_indexable")
    @Expose
    var isRobotIndexable: Boolean? = null
    @SerializedName("report_reasons")
    @Expose
    var reportReasons: String? = null
    @SerializedName("author")
    @Expose
    var author: String? = null
    @SerializedName("num_crossposts")
    @Expose
    var numCrossposts: Int? = null
    @SerializedName("num_comments")
    @Expose
    var numComments: Int? = null
    @SerializedName("send_replies")
    @Expose
    var sendReplies: Boolean? = null
    @SerializedName("whitelist_status")
    @Expose
    var whitelistStatus: String? = null
    @SerializedName("contest_mode")
    @Expose
    var contestMode: Boolean? = null
    @SerializedName("mod_reports")
    @Expose
    var modReports: List<Any>? = null
    @SerializedName("author_patreon_flair")
    @Expose
    var authorPatreonFlair: Boolean? = null
    @SerializedName("author_flair_text_color")
    @Expose
    var authorFlairTextColor: String? = null
    @SerializedName("permalink")
    @Expose
    var permalink: String? = null
    @SerializedName("parent_whitelist_status")
    @Expose
    var parentWhitelistStatus: String? = null
    @SerializedName("stickied")
    @Expose
    var stickied: Boolean? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("subreddit_subscribers")
    @Expose
    var subredditSubscribers: Int? = null
    @SerializedName("created_utc")
    @Expose
    var createdUtc: Double? = null
    @SerializedName("media")
    @Expose
    var media: Media? = null
    @SerializedName("is_video")
    @Expose
    var isVideo: Boolean? = null

}

class Variants {

    @SerializedName("gif")
    @Expose
    var gif: VideoType? = null
    @SerializedName("mp4")
    @Expose
    var mp4: VideoType? = null

}

class VideoType {

    @SerializedName("source")
    @Expose
    var source: Source? = null
    @SerializedName("resolutions")
    @Expose
    var resolutions: List<Source>? = null

}

class Source {

    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null

}

class PostInfoListWrapper {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: PostInfoList? = null

}

class RedditVideo {

    @SerializedName("fallback_url")
    @Expose
    var fallbackUrl: String? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("scrubber_media_url")
    @Expose
    var scrubberMediaUrl: String? = null
    @SerializedName("dash_url")
    @Expose
    var dashUrl: String? = null
    @SerializedName("duration")
    @Expose
    var duration: Int? = null
    @SerializedName("hls_url")
    @Expose
    var hlsUrl: String? = null
    @SerializedName("is_gif")
    @Expose
    var isGif: Boolean? = null
    @SerializedName("transcoding_status")
    @Expose
    var transcodingStatus: String? = null

}

class Preview {

    @SerializedName("images")
    @Expose
    var images: List<RedditImage>? = null
    @SerializedName("reddit_video_preview")
    @Expose
    var redditVideoPreview: RedditVideo? = null
    @SerializedName("enabled")
    @Expose
    var enabled: Boolean? = null

}

class MediaEmbed {

    @SerializedName("content")
    @Expose
    var content: String? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("scrolling")
    @Expose
    var scrolling: Boolean? = null
    @SerializedName("media_domain_url")
    @Expose
    var mediaDomainUrl: String? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null

}

class Media {

    @SerializedName("reddit_video")
    @Expose
    var redditVideo: RedditVideo? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("oembed")
    @Expose
    var oembed: Oembed? = null

}

class Oembed {

    @SerializedName("provider_url")
    @Expose
    var providerUrl: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("thumbnail_width")
    @Expose
    var thumbnailWidth: Int? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("html")
    @Expose
    var html: String? = null
    @SerializedName("version")
    @Expose
    var version: String? = null
    @SerializedName("author_name")
    @Expose
    var authorName: String? = null
    @SerializedName("provider_name")
    @Expose
    var providerName: String? = null
    @SerializedName("thumbnail_url")
    @Expose
    var thumbnailUrl: String? = null
    @SerializedName("thumbnail_height")
    @Expose
    var thumbnailHeight: Int? = null
    @SerializedName("author_url")
    @Expose
    var authorUrl: String? = null

}

class Richtext {

    @SerializedName("e")
    @Expose
    var e: String? = null
    @SerializedName("t")
    @Expose
    var t: String? = null

}

class RedditImage {

    @SerializedName("source")
    @Expose
    var source: Source? = null
    @SerializedName("resolutions")
    @Expose
    var resolutions: List<Source>? = null
    @SerializedName("variants")
    @Expose
    var variants: Variants? = null
    @SerializedName("redditId")
    @Expose
    var id: String? = null

}

class Gildings

class PostInfoList {

    @SerializedName("modhash")
    @Expose
    var modhash: String? = null
    @SerializedName("dist")
    @Expose
    var dist: Int? = null
    @SerializedName("children")
    @Expose
    var children: List<PostInfoWrapper>? = null
    @SerializedName("after")
    @Expose
    var after: String? = null
    @SerializedName("before")
    @Expose
    var before: String? = null

}

class PostInfoWrapper {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: PostInfo? = null

}

class AllAwarding {

    @SerializedName("is_enabled")
    @Expose
    var isEnabled: Boolean? = null
    @SerializedName("count")
    @Expose
    var count: Int? = null
    @SerializedName("subreddit_id")
    @Expose
    var subredditId: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("coin_reward")
    @Expose
    var coinReward: Int? = null
    @SerializedName("icon_width")
    @Expose
    var iconWidth: Int? = null
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null
    @SerializedName("days_of_premium")
    @Expose
    var daysOfPremium: Int? = null
    @SerializedName("icon_height")
    @Expose
    var iconHeight: Int? = null
    @SerializedName("resized_icons")
    @Expose
    var resizedIcons: List<Source>? = null
    @SerializedName("days_of_drip_extension")
    @Expose
    var daysOfDripExtension: Int? = null
    @SerializedName("award_type")
    @Expose
    var awardType: String? = null
    @SerializedName("coin_price")
    @Expose
    var coinPrice: Int? = null
    @SerializedName("redditId")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}

class MultiInfoBasic {

    @SerializedName("can_edit")
    @Expose
    var canEdit: Boolean? = null
    @SerializedName("display_name")
    @Expose
    var displayName: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("description_html")
    @Expose
    var descriptionHtml: String? = null
    @SerializedName("num_subscribers")
    @Expose
    var numSubscribers: Int? = null
    @SerializedName("copied_from")
    @Expose
    var copiedFrom: String? = null
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null
    @SerializedName("subreddits")
    @Expose
    var subreddits: List<SubredditBasic>? = null
    @SerializedName("created_utc")
    @Expose
    var createdUtc: Double? = null
    @SerializedName("visibility")
    @Expose
    var visibility: String? = null
    @SerializedName("created")
    @Expose
    var created: Double? = null
    @SerializedName("over_18")
    @Expose
    var over18: Boolean? = null
    @SerializedName("path")
    @Expose
    var path: String? = null
    @SerializedName("owner")
    @Expose
    var owner: String? = null
    @SerializedName("key_color")
    @Expose
    var keyColor: String? = null
    @SerializedName("is_subscriber")
    @Expose
    var isSubscriber: Boolean? = null
    @SerializedName("owner_id")
    @Expose
    var ownerId: String? = null
    @SerializedName("description_md")
    @Expose
    var descriptionMd: String? = null
    @SerializedName("is_favorited")
    @Expose
    var isFavorited: Boolean? = null

}

class MultiInfoWrapperBasic {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: MultiInfoBasic? = null

}

class MultiInfo {

    @SerializedName("can_edit")
    @Expose
    var canEdit: Boolean? = null
    @SerializedName("display_name")
    @Expose
    var displayName: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("description_html")
    @Expose
    var descriptionHtml: String? = null
    @SerializedName("num_subscribers")
    @Expose
    var numSubscribers: Int? = null
    @SerializedName("copied_from")
    @Expose
    var copiedFrom: String? = null
    @SerializedName("icon_url")
    @Expose
    var iconUrl: String? = null
    @SerializedName("subreddits")
    @Expose
    var subreddits: List<SubredditInfoWrapper>? = null
    @SerializedName("created_utc")
    @Expose
    var createdUtc: Double? = null
    @SerializedName("visibility")
    @Expose
    var visibility: String? = null
    @SerializedName("created")
    @Expose
    var created: Double? = null
    @SerializedName("over_18")
    @Expose
    var over18: Boolean? = null
    @SerializedName("path")
    @Expose
    var path: String? = null
    @SerializedName("owner")
    @Expose
    var owner: String? = null
    @SerializedName("key_color")
    @Expose
    var keyColor: String? = null
    @SerializedName("is_subscriber")
    @Expose
    var isSubscriber: Boolean? = null
    @SerializedName("owner_id")
    @Expose
    var ownerId: String? = null
    @SerializedName("description_md")
    @Expose
    var descriptionMd: String? = null
    @SerializedName("is_favorited")
    @Expose
    var isFavorited: Boolean? = null

}

class MultiInfoWrapper {

    @SerializedName("kind")
    @Expose
    var kind: String? = null
    @SerializedName("data")
    @Expose
    var data: MultiInfo? = null

}

class SubredditBasic {

    @SerializedName("name")
    @Expose
    var name: String? = null

}
