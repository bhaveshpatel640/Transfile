function loadSystemInfo() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var info = JSON.parse(xhttp.responseText);
            document.getElementById("device_model").innerHTML = info.DeviceModel;
            document.getElementById("DeviceModelName").innerHTML = info.DeviceModel;
            document.getElementById("battery_power").innerHTML = info.BatteryPower + "%";
            document.getElementById("charging_status").innerHTML = info.ChargingStatus;
            document.getElementById("wifi").innerHTML = info.Wifi;

            document.getElementById("video_count").innerHTML = info.Videos;
            document.getElementById("video_size").innerHTML = pharseFileSize(info.VideoSize);

            document.getElementById("image_count").innerHTML = info.Pictures;
            document.getElementById("image_size").innerHTML = pharseFileSize(info.PictureSize);

            document.getElementById("music_count").innerHTML = info.Musics;
            document.getElementById("music_size").innerHTML = pharseFileSize(info.MusicSize);
            document.getElementById("used_memory").innerHTML = pharseFileSize(info.PhoneMemorySize - info.PhoneMemoryLeft);

            document.getElementById("total_memory").innerHTML = pharseFileSize(info.PhoneMemorySize);

            document.getElementById("progress_meter").style.width = ((info.PhoneMemorySize - info.PhoneMemoryLeft) / info.PhoneMemorySize) * 100 + "%";
            /*
            document.getElementById("file_count").innerHTML = info.DeviceModel;
            document.getElementById("file_size").innerHTML = info.DeviceModel;*/

            //            alert(xhttp.responseText);
        } else {
            // alert("Error");
        }


        function pharseFileSize(lengthBytes) {
            var size;
            if (lengthBytes <= 1024) {
                return lengthBytes.toFixed(2) + " bytes";
            }
            var m = lengthBytes;
            var cont = 0;

            while (m >= 1024) {
                cont++;
                m = m / 1024;
            }

            switch (cont) {
            case 1: // KB
                size = " KB";
                break;
            case 2: // MB
                size = " MB";
                break;
            case 3: // GB
                size = " GB";
                break;
            case 4: // TB
                size = " TB";
                break;
            default:
                size = " B";
            }
            return ((lengthBytes / (Math.pow(1024, cont)))).toFixed(2) + size;
        }
    };
    xhttp.open("GET", "/?Key=PhoneSystemInfo", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function loadPhotos() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var photoList = JSON.parse(xhttp.responseText);
            var innerDiv = "";
            var i;
            for (i = 0; i < photoList.length; i++) {
                if (i % 4 == 0)
                    innerDiv += "<div class='row'>";
                var photo = photoList[i];
                innerDiv += '<div class="small-3 columns nopadding">' +
                    '<div class="image-wrapper overlay-fade-in" style="width:200px;height:200px;">' +
                    '<img id="image_' + i + '" src="' + decodeURI(photo.path) + '?Thumbnail=1&Width=500' +
                    '" />' +
                    '<div class="image-overlay-content">' +
                    '<h2><a href="' + decodeURI(photo.path) + '" download><i class="fa fa-download" style="margin-right:15px;"></i></a>' + '<a  onclick="modalImageDetails(\'' + photo.showName + '\',\'' + photo.size + '\',\'' + photo.width + '\',\'' + photo.height + '\',\'' + photo.path + '\',\'' + photo.modifyTime + '\')"><i class="fa fa-info-circle"></i></a></h2>' + '<a onclick="showImage(\'image_' + i + '\');">' +
                    '<div style="width:100%;height:100%;"></div></a></div></div></div>';
                if (i % 4 == 3) {
                    innerDiv += "</div>";
                }
            }

            if (i % 4 != 0) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div>';
            } else if (i % 4 != 1) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div>';
            } else if (i % 4 != 2) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div>';
            }
            document.getElementById("photos_list").innerHTML = innerDiv;
        } else {
            //alert("Error");
        }
    };

    xhttp.open("GET", "/?Key=PhotoList", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function loadVideos() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var videoList = JSON.parse(xhttp.responseText);
            var innerDiv = "";
            var i;
            for (i = 0; i < videoList.length; i++) {
                if (i % 4 == 0)
                    innerDiv += "<div class='row'>";

                var video = videoList[i];
                innerDiv += '<div class="small-3 columns">' +
                    '<div class="image-wrapper overlay-fade-in" style="width:200px;height:200px;">' +
                    '<img id="video_' + i + '" src="' + decodeURI(video.path) + '?VideoThumbnail=1" />' +
                    '<div class="image-overlay-content">' +
                    '<h2>' +
                    '<a href="' + decodeURI(video.path) + '" download><i class="fa fa-download" style="margin-right:15px;"></i></a>' + '<a  onclick="modalImageDetails(\'' + video.showName + '\',\'' + video.size + '\',\'' + video.width + '\',\'' + video.height + '\',\'' + video.path + '\',\'' + video.modifyTime + '\')"><i class="fa fa-info-circle"></i></a>' + '</h2><a onclick="showVideo(\'video_' + i + '\');">' +
                    '<div style="width:100%;height:100%;"></div></a></div></div></div>';
                if (i % 4 == 3)
                    innerDiv += "</div>";

            }
            if (i % 4 != 0) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div>';
            } else if (i % 4 != 1) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div><div class="small-3 columns" style="display:none;"></div>';
            } else if (i % 4 != 2) {
                innerDiv += '<div class="small-3 columns" style="display:none;"></div>';
            }
            document.getElementById("videos_list").innerHTML = innerDiv;
        } else {
            //alert("Error");
        }
    };

    xhttp.open("GET", "/?Key=VideoList", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function loadMusics() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var musicList = JSON.parse(xhttp.responseText);
            var innerDiv = "";
            var i;
            for (i = 0; i < musicList.length; i++) {
                var music = musicList[i];
                innerDiv += '<tr>' +
                    '<td class="text-center" width="300">' + music.showName + '</td>' +
                    '<td class="text-center" width="130">' + music.artist + '</td>' +
                    '<td class="text-center" width="100">' + msToTime(music.duration) + '</td>' +
                    '<td class="text-center" width="100">' + pharseFileSize(music.size) + '</td>' +
                    '<td width="120" class="text-center"><a href="' + decodeURI(music.path) + '" download><i class="fa fa-download fa-large"></i></a>' +
                    '</td><td width="120" class="text-center">' +
                    '<a onclick=\'playmusic("' + music.showName + '","' + music.artist + '","' + decodeURI(music.path) + '");\'>' +
                    '<i class="fa fa-play"></i></a>' +
                    '</td></tr>';
            }
            document.getElementById("musics_list").innerHTML = innerDiv;
        } else {
            //alert("Error");
        }
    };

    xhttp.open("GET", "/?Key=MusicList", true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}

function loadFiles(dirPath) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var files = JSON.parse(xhttp.responseText);
            var i, innerDirectoryPath;

            var directoryList = files[0].directoryPath;
            var temp = new Array();
            temp = directoryList.split("/");

            for (i = 0; i < temp.length; i++) {
                innerDirectoryPath += '<span class="text-center">' + temp[0] + '</span>';
                if (i < temp.length - 1) {
                    innerDirectoryPath += '/';
                }
            }

            var innerDiv = "";
            for (i = 1; i < files.length; i++) {
                var file = files[i];
                var file_name,download;
                var size, extension;
                if (file.fileType == 1) {
                    size = pharseFileSize(file.size);
                    extension = file.extension;
                    file_name = '<td width="400px" style="padding-left: 20px;">' + file.showName + '</td>';
                    download ='<td class="text-center" width="100px"><a href=\'' + file.path + '\' download><i class="fa fa-download fa-large"></i></a></td>';
                } else {
                    size = "";
                    extension = "FOLDER";
                    file_name = '<td width="400px" style="padding-left: 20px;"><a onclick="loadFiles(\'' + file.path + '\')">' + file.showName + '</a></td>';
                    download = '<td class="text-center" width="100px"><a href=\'' + file.path + '?download=zip\' download><i class="fa fa-download fa-large"></i></a></td>';
                }
                
                innerDiv += '<tr>' + file_name +
                    '<td class="text-center" width="100px">' + extension + '</td>' +
                    '<td class="text-center" width="100px">' + size + '</td>' +
                    '<td class="text-center" width="250px">' + timeConverter(file.modifyTime) + '</td>' +
                    download +
                    '</tr>';

            }
            document.getElementById("directoryList").innerHTML = innerDirectoryPath;
            document.getElementById("files_list").innerHTML = innerDiv;
        } else {
            //alert("Error");
        }
    };

    xhttp.open("GET", decodeURI(dirPath), true);
    xhttp.setRequestHeader("Content-type", "text/html");
    xhttp.send();
}


function playmusic(musicTitle, musicAuthor, musicPath) {
    document.getElementById('player').innerHTML = "";
    var ap = new APlayer({
        element: document.getElementById('player')
        , narrow: false
        , autoplay: false
        , showlrc: false
        , theme: '#ad7a86'
        , music: [
            {
                title: musicTitle
                , author: musicAuthor
                , url: musicPath
                , pic: "/web/images/musicArt.png"
                        }
                            ]
    });
    ap.init();
    ap.play();
}

function msToTime(duration) {
    var milliseconds = parseInt((duration % 1000) / 100)
        , seconds = parseInt((duration / 1000) % 60)
        , minutes = parseInt((duration / (1000 * 60)) % 60)
        , hours = parseInt((duration / (1000 * 60 * 60)) % 24);

    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;

    return hours + ":" + minutes + ":" + seconds;
}

function pharseFileSize(lengthBytes) {
    var size;
    if (lengthBytes <= 1024) {
        return lengthBytes.toFixed(2) + " bytes";
    }
    var m = lengthBytes;
    var cont = 0;

    while (m >= 1024) {
        cont++;
        m = m / 1024;
    }

    switch (cont) {
    case 1: // KB
        size = " KB";
        break;
    case 2: // MB
        size = " MB";
        break;
    case 3: // GB
        size = " GB";
        break;
    case 4: // TB
        size = " TB";
        break;
    default:
        size = " B";
    }
    return ((lengthBytes / (Math.pow(1024, cont)))).toFixed(2) + size;
}

function timeConverter(UNIX_timestamp) {
    var d = new Date(parseInt(UNIX_timestamp));
    return d.getFullYear() + '-' + d.getMonth() + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes();
}