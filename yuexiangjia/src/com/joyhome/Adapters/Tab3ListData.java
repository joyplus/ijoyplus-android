package com.joyhome.Adapters;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Tab3ListData  implements Parcelable{
	public int _id;
	public String _data;
	public String _display_name;
	public int _size;
	public String mime_type;
	public String date_added;
	public int is_drm;
	public String date_modified;
	public String title;
	public String title_key;
	public int duration;
	public int artist_id;
	public String composer;
	public int album_id;
	public String track;
	public String year;
	public int is_ringtone;
	public int is_music;
	public int is_alarm;
	public int is_notification;
	public int is_podcast;
	public String bookmark;
	public String album_artist;
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public Tab3ListData() {
		
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(_id);
		arg0.writeString(_data);
		arg0.writeString(_display_name);
		arg0.writeInt(_size);
		arg0.writeString(mime_type);
		arg0.writeString(date_added);
		arg0.writeInt(is_drm);
		arg0.writeString(date_modified);
		arg0.writeString(title);
		arg0.writeString(title_key);
		arg0.writeInt(duration);
		arg0.writeInt(artist_id);
		arg0.writeString(composer);
		arg0.writeInt(album_id);
		arg0.writeString(track);
		arg0.writeString(year);
		arg0.writeInt(is_ringtone);
		arg0.writeInt(is_music);
		arg0.writeInt(is_alarm);
		arg0.writeInt(is_notification);
		arg0.writeInt(is_podcast);
		arg0.writeString(bookmark);
		arg0.writeString(album_artist);
	}
	public static final Parcelable.Creator<Tab3ListData> CREATOR = new Creator<Tab3ListData>() {    
        public Tab3ListData createFromParcel(Parcel source) {    
        	Tab3ListData m_Tab3ListData = new Tab3ListData();    
       	 m_Tab3ListData._id = source.readInt();
       	 m_Tab3ListData._data = source.readString();
       	 m_Tab3ListData._display_name = source.readString();
       	 m_Tab3ListData._size = source.readInt();
       	 m_Tab3ListData.mime_type = source.readString();
       	 m_Tab3ListData.date_added = source.readString();
       	 m_Tab3ListData.is_drm = source.readInt();
       	 m_Tab3ListData.date_modified = source.readString();
       	 m_Tab3ListData.title = source.readString();
       	 m_Tab3ListData.title_key = source.readString();
       	 m_Tab3ListData.duration = source.readInt();
       	 m_Tab3ListData.artist_id = source.readInt();
       	 m_Tab3ListData.composer = source.readString();
       	 m_Tab3ListData.album_id = source.readInt();
       	 m_Tab3ListData.track = source.readString();
       	 m_Tab3ListData.year = source.readString();
       	 m_Tab3ListData.is_ringtone = source.readInt();
       	 m_Tab3ListData.is_music = source.readInt();
       	 m_Tab3ListData.is_alarm = source.readInt();
       	 m_Tab3ListData.is_notification = source.readInt();
       	 m_Tab3ListData.is_podcast = source.readInt();
       	 m_Tab3ListData.bookmark = source.readString();
       	 m_Tab3ListData.album_artist = source.readString();
            return m_Tab3ListData;    
        }    
        public Tab3ListData[] newArray(int size) {    
            return new Tab3ListData[size];    
        }    
    };    
}
