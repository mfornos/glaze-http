package marmalade.examples.twitter.api.stream;

import java.util.Date;
import java.util.Set;

import lombok.Data;
import marmalade.examples.twitter.api.TwitterDateDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
public class User
{
   @JsonProperty("contributors_enabled")
   private Boolean contributorsEnabled;
   @JsonDeserialize(using = TwitterDateDeserializer.class)
   @JsonProperty("created_at")
   private Date createdAt;
   private Entities entities;
   @JsonProperty("default_profile")
   private Boolean defaultProfile;
   @JsonProperty("default_profile_image")
   private Boolean defaultProfileImage;
   private String description;
   @JsonProperty("favourites_count")
   private Integer favouritesCount;
   @JsonProperty("follow_request_sent")
   private Boolean followRequestSent;
   @JsonProperty("followers_count")
   private Integer followersCount;
   @JsonProperty("friends_count")
   private Integer friendsCount;
   @JsonProperty("geo_enabled")
   private Boolean geoEnabled;
   private Long id;
   @JsonProperty("id_str")
   private String idStr;
   @JsonProperty("is_translator")
   private Boolean isTranslator;
   private String lang;
   @JsonProperty("listed_count")
   private Integer listedCount;
   private String name;
   private String location;
   private Boolean notifications;
   @JsonProperty("profile_background_color")
   private String profileBackgroundColor;
   @JsonProperty("profile_background_image_url")
   private String profileBackgroundImageUrl;
   @JsonProperty("profile_background_image_url_https")
   private String profileBackgroundImageUrlHttps;
   @JsonProperty("profile_background_tile")
   private Boolean profileBackgroundTile;
   @JsonProperty("profile_banner_url")
   private String profileBannerUrl;
   @JsonProperty("profile_image_url")
   private String profileImageUrl;
   @JsonProperty("profile_image_url_https")
   private String profileImageUrlHttps;
   @JsonProperty("profile_link_color")
   private String profileLinkColor;
   @JsonProperty("profile_sidebar_border_color")
   private String profileSidebarBorderColor;
   @JsonProperty("profile_sidebar_fill_color")
   private String profileSidebarFillColor;
   @JsonProperty("profile_text_color")
   private String profileTextColor;
   @JsonProperty("profile_use_background_image")
   private Boolean profileUseBackgroundImage;
   @JsonProperty("protected")
   private Boolean isProtected;
   @JsonProperty("screen_name")
   private String screenName;
   @JsonProperty("show_all_inline_media")
   private Boolean showAllInlineMedia;
   private Set<Tweet> status;
   @JsonProperty("statuses_count")
   private Integer statusesCount;
   @JsonProperty("time_zone")
   private String timeZone;
   private String url;
   @JsonProperty("utc_offset")
   private Integer utcOffset;
   private Boolean verified;
   @JsonProperty("withheld_in_countries")
   private String withheldInCountries;
   @JsonProperty("withheld_scope")
   private String withheldScope;
}
