package com.twt.launcheros.utils

object Constants {

    object Key {
        const val FLAG = "flag"
        const val RENAME = "rename"
    }

    object Dialog {
        const val ABOUT = "ABOUT"
        const val WALLPAPER = "WALLPAPER"
        const val REVIEW = "REVIEW"
        const val RATE = "RATE"
        const val SHARE = "SHARE"
        const val HIDDEN = "HIDDEN"
        const val KEYBOARD = "KEYBOARD"
        const val DIGITAL_WELLBEING = "DIGITAL_WELLBEING"
        const val PRO_MESSAGE = "PRO_MESSAGE"
    }

    object UserState {
        const val START = "START"
        const val WALLPAPER = "WALLPAPER"
        const val REVIEW = "REVIEW"
        const val RATE = "RATE"
        const val SHARE = "SHARE"
    }

    object DateTime {
        const val OFF = 0
        const val ON = 1
        const val DATE_ONLY = 2

        fun isTimeVisible(dateTimeVisibility: Int): Boolean {
            return dateTimeVisibility == ON
        }

        fun isDateVisible(dateTimeVisibility: Int): Boolean {
            return dateTimeVisibility == ON || dateTimeVisibility == DATE_ONLY
        }
    }

    object SwipeDownAction {
        const val SEARCH = 1
        const val NOTIFICATIONS = 2
    }

    object TextSize {
        const val ONE = 0.6f
        const val TWO = 0.75f
        const val THREE = 0.9f
        const val FOUR = 1f
        const val FIVE = 1.1f
        const val SIX = 1.2f
        const val SEVEN = 1.3f
    }

    object CharacterIndicator {
        const val SHOW = 102
        const val HIDE = 101
    }

    const val WALL_TYPE_LIGHT = "light"
    const val WALL_TYPE_DARK = "dark"

    const val THEME_MODE_DARK = 0
    const val THEME_MODE_LIGHT = 1
    const val THEME_MODE_SYSTEM = 2

    const val FLAG_LAUNCH_APP = 100
    const val FLAG_HIDDEN_APPS = 101

    const val FLAG_SET_HOME_APP_1 = 1
    const val FLAG_SET_HOME_APP_2 = 2
    const val FLAG_SET_HOME_APP_3 = 3
    const val FLAG_SET_HOME_APP_4 = 4
    const val FLAG_SET_HOME_APP_5 = 5
    const val FLAG_SET_HOME_APP_6 = 6
    const val FLAG_SET_HOME_APP_7 = 7
    const val FLAG_SET_HOME_APP_8 = 8

    const val FLAG_SET_SWIPE_LEFT_APP = 11
    const val FLAG_SET_SWIPE_RIGHT_APP = 12
    const val FLAG_SET_CLOCK_APP = 13
    const val FLAG_SET_CALENDAR_APP = 14

    const val REQUEST_CODE_ENABLE_ADMIN = 666
    const val REQUEST_CODE_LAUNCHER_SELECTOR = 678

    const val HINT_RATE_US = 15

    const val LONG_PRESS_DELAY_MS = 500L
    const val ONE_DAY_IN_MILLIS = 86400000L
    const val ONE_HOUR_IN_MILLIS = 3600000L
    const val ONE_MINUTE_IN_MILLIS = 60000L

    const val MIN_ANIM_REFRESH_RATE = 10f

    const val URL_ABOUT_OLAUNCHER =
        "https://tanujnotes.substack.com/p/olauncher-minimal-af-launcher?utm_source=olauncher"
    const val URL_OLAUNCHER_PRIVACY =
        "https://tanujnotes.notion.site/Olauncher-Privacy-Policy-dd6ac5101ddd4b3da9d27057889d44ab"
    const val URL_DOUBLE_TAP =
        "https://tanujnotes.notion.site/Double-tap-to-lock-Olauncher-0f7fb103ec1f47d7a90cdfdcd7fb86ef"
    const val URL_OLAUNCHER_GITHUB = "https://www.github.com/tanujnotes/Olauncher"
    const val URL_OLAUNCHER_PLAY_STORE =
        "https://play.google.com/store/apps/details?id=app.olauncher"
    const val URL_OLAUNCHER_PRO = "https://play.google.com/store/apps/details?id=app.prolauncher"
    const val URL_PLAY_STORE_DEV = "https://play.google.com/store/apps/dev?id=7198807840081074933"
    const val URL_TWITTER_TANUJ = "https://twitter.com/tanujnotes"
    const val URL_DEFAULT_DARK_WALLPAPER =
        "https://images.unsplash.com/photo-1512551980832-13df02babc9e"
    const val URL_DEFAULT_LIGHT_WALLPAPER =
        "https://images.unsplash.com/photo-1515549832467-8783363e19b6"
    const val URL_DUCK_SEARCH = "https://duck.co/?q="
    const val URL_DIGITAL_WELLBEING_LEARN_MORE =
        "https://tanujnotes.substack.com/p/digital-wellbeing-app-on-android?utm_source=olauncher"

    const val DIGITAL_WELLBEING_PACKAGE_NAME = "com.google.android.apps.wellbeing"
    const val DIGITAL_WELLBEING_ACTIVITY =
        "com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity"
    const val DIGITAL_WELLBEING_SAMSUNG_PACKAGE_NAME = "com.samsung.android.forest"
    const val DIGITAL_WELLBEING_SAMSUNG_ACTIVITY =
        "com.samsung.android.forest.launcher.LauncherActivity"
    const val WALLPAPER_WORKER_NAME = "WALLPAPER_WORKER_NAME"

    class Prefs {
        companion object {
            const val FIRST_OPEN = "FIRST_OPEN"
            const val FIRST_OPEN_TIME = "FIRST_OPEN_TIME"
            const val FIRST_SETTINGS_OPEN = "FIRST_SETTINGS_OPEN"
            const val FIRST_HIDE = "FIRST_HIDE"
            const val USER_STATE = "USER_STATE"
            const val LOCK_MODE = "LOCK_MODE"
            const val HOME_APPS_NUM = "HOME_APPS_NUM"
            const val AUTO_SHOW_KEYBOARD = "AUTO_SHOW_KEYBOARD"
            const val KEYBOARD_MESSAGE = "KEYBOARD_MESSAGE"
            const val DAILY_WALLPAPER = "DAILY_WALLPAPER"
            const val DAILY_WALLPAPER_URL = "DAILY_WALLPAPER_URL"
            const val WALLPAPER_UPDATED_DAY = "WALLPAPER_UPDATED_DAY"
            const val HOME_ALIGNMENT = "HOME_ALIGNMENT"
            const val HOME_BOTTOM_ALIGNMENT = "HOME_BOTTOM_ALIGNMENT"
            const val APP_LABEL_ALIGNMENT = "APP_LABEL_ALIGNMENT"
            const val STATUS_BAR = "STATUS_BAR"
            const val DATE_TIME_VISIBILITY = "DATE_TIME_VISIBILITY"
            const val SWIPE_LEFT_ENABLED = "SWIPE_LEFT_ENABLED"
            const val SWIPE_RIGHT_ENABLED = "SWIPE_RIGHT_ENABLED"
            const val HIDDEN_APPS = "HIDDEN_APPS"
            const val HIDDEN_APPS_UPDATED = "HIDDEN_APPS_UPDATED"
            const val SHOW_HINT_COUNTER = "SHOW_HINT_COUNTER"
            const val APP_THEME = "APP_THEME"
            const val ABOUT_CLICKED = "ABOUT_CLICKED"
            const val RATE_CLICKED = "RATE_CLICKED"
            const val WALLPAPER_MSG_SHOWN = "WALLPAPER_MSG_SHOWN"
            const val SHARE_SHOWN_TIME = "SHARE_SHOWN_TIME"
            const val SWIPE_DOWN_ACTION = "SWIPE_DOWN_ACTION"
            const val TEXT_SIZE_SCALE = "TEXT_SIZE_SCALE"
            const val PRO_MESSAGE_SHOWN = "PRO_MESSAGE_SHOWN"
            const val HIDE_SET_DEFAULT_LAUNCHER = "HIDE_SET_DEFAULT_LAUNCHER"
            const val SCREEN_TIME_LAST_UPDATED = "SCREEN_TIME_LAST_UPDATED"

            const val APP_NAME_1 = "APP_NAME_1"
            const val APP_NAME_2 = "APP_NAME_2"
            const val APP_NAME_3 = "APP_NAME_3"
            const val APP_NAME_4 = "APP_NAME_4"
            const val APP_NAME_5 = "APP_NAME_5"
            const val APP_NAME_6 = "APP_NAME_6"
            const val APP_NAME_7 = "APP_NAME_7"
            const val APP_NAME_8 = "APP_NAME_8"
            const val APP_PACKAGE_1 = "APP_PACKAGE_1"
            const val APP_PACKAGE_2 = "APP_PACKAGE_2"
            const val APP_PACKAGE_3 = "APP_PACKAGE_3"
            const val APP_PACKAGE_4 = "APP_PACKAGE_4"
            const val APP_PACKAGE_5 = "APP_PACKAGE_5"
            const val APP_PACKAGE_6 = "APP_PACKAGE_6"
            const val APP_PACKAGE_7 = "APP_PACKAGE_7"
            const val APP_PACKAGE_8 = "APP_PACKAGE_8"
            const val APP_ACTIVITY_CLASS_NAME_1 = "APP_ACTIVITY_CLASS_NAME_1"
            const val APP_ACTIVITY_CLASS_NAME_2 = "APP_ACTIVITY_CLASS_NAME_2"
            const val APP_ACTIVITY_CLASS_NAME_3 = "APP_ACTIVITY_CLASS_NAME_3"
            const val APP_ACTIVITY_CLASS_NAME_4 = "APP_ACTIVITY_CLASS_NAME_4"
            const val APP_ACTIVITY_CLASS_NAME_5 = "APP_ACTIVITY_CLASS_NAME_5"
            const val APP_ACTIVITY_CLASS_NAME_6 = "APP_ACTIVITY_CLASS_NAME_6"
            const val APP_ACTIVITY_CLASS_NAME_7 = "APP_ACTIVITY_CLASS_NAME_7"
            const val APP_ACTIVITY_CLASS_NAME_8 = "APP_ACTIVITY_CLASS_NAME_8"
            const val APP_USER_1 = "APP_USER_1"
            const val APP_USER_2 = "APP_USER_2"
            const val APP_USER_3 = "APP_USER_3"
            const val APP_USER_4 = "APP_USER_4"
            const val APP_USER_5 = "APP_USER_5"
            const val APP_USER_6 = "APP_USER_6"
            const val APP_USER_7 = "APP_USER_7"
            const val APP_USER_8 = "APP_USER_8"

            const val APP_NAME_SWIPE_LEFT = "APP_NAME_SWIPE_LEFT"
            const val APP_NAME_SWIPE_RIGHT = "APP_NAME_SWIPE_RIGHT"
            const val APP_PACKAGE_SWIPE_LEFT = "APP_PACKAGE_SWIPE_LEFT"
            const val APP_PACKAGE_SWIPE_RIGHT = "APP_PACKAGE_SWIPE_RIGHT"
            const val APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT = "APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT"
            const val APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT = "APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT"
            const val APP_USER_SWIPE_LEFT = "APP_USER_SWIPE_LEFT"
            const val APP_USER_SWIPE_RIGHT = "APP_USER_SWIPE_RIGHT"
            const val CLOCK_APP_PACKAGE = "CLOCK_APP_PACKAGE"
            const val CLOCK_APP_USER = "CLOCK_APP_USER"
            const val CLOCK_APP_CLASS_NAME = "CLOCK_APP_CLASS_NAME"
            const val CALENDAR_APP_PACKAGE = "CALENDAR_APP_PACKAGE"
            const val CALENDAR_APP_USER = "CALENDAR_APP_USER"
            const val CALENDAR_APP_CLASS_NAME = "CALENDAR_APP_CLASS_NAME"
        }
    }


    // Used to load the 'launcheros' library on application startup.
    init {
        System.loadLibrary("launcheros")
    }
    external fun urlWallpapers(): String
}