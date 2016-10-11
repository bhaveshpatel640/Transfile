package com.wireless.transfile.constants;

public class Constants {
    public static final String MESSAGE = "message";
    public static final String IS_SERVICE_STARTED = "isServiceStarted";
    public static final String PREF_SERVER_PORT = "prefServerPort";
    public static final int DEFAULT_SERVER_PORT = 4567;
    public static final String ACCEPT_REQUEST = "acceptRequest";
    public static final boolean DEFAULT_ACCEPT_REQUEST = false;
    public static final Boolean LOG_DEBUG = false;
    public static final String[][] EXTENSIONS =
            {
            /*Image*/
                    {
                            ".tif", ".tiff", ".gif", ".jpeg", ".jpg", ".jif", ".jfif", ".png", ".gif", ".jpeg", ".jpg", ".jif"
                    },
            /*Video*/
                    {
                            ".webm", ".mp4", ".ogg"
                    },
            /*Music*/
                    {
                            ".mp3", ".ogg", ".wav", ".acc"
                    },
            /*Documents*/
                    {
                            ".doc", ".docx", ".pdf", ".pages", ".pub", ".epub", ".xps", ".odt", ".dotx", ".dot", ".ppt", ".vcard", ".dox"
                    }
            };


}
