package com.joyhome.Adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class Tab1GridData  implements Parcelable {
	public int _id;
	public String _data;
	public String _size;
	public String _display_name;
	public String mime_type;
	public String title;
	public String date_added;
	public String date_modified;
	public int bucket_id;
	public String bucket_display_name;
	public String width;
	public String height;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public Tab1GridData() {
		
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(_id);
		arg0.writeString(_data);
		arg0.writeString(_size);
		arg0.writeString(_display_name);
		arg0.writeString(mime_type);
		arg0.writeString(title);
		arg0.writeString(date_added);
		arg0.writeString(date_modified);
		arg0.writeInt(bucket_id);
		arg0.writeString(bucket_display_name);
		arg0.writeString(width);
		arg0.writeString(height);
	}
	public static final Parcelable.Creator<Tab1GridData> CREATOR = new Creator<Tab1GridData>() {    
        public Tab1GridData createFromParcel(Parcel source) {    
            Tab1GridData m_Tab1GridData = new Tab1GridData();    
            m_Tab1GridData._id = source.readInt();
    		m_Tab1GridData._data = source.readString();
    		m_Tab1GridData._size = source.readString();
    		m_Tab1GridData._display_name = source.readString();
    		m_Tab1GridData.mime_type = source.readString();
    		m_Tab1GridData.title = source.readString();
    		m_Tab1GridData.date_added = source.readString();
    		m_Tab1GridData.date_modified = source.readString();
    		m_Tab1GridData.bucket_id = source.readInt();
    		m_Tab1GridData.bucket_display_name = source.readString();
    		m_Tab1GridData.width = source.readString();
    		m_Tab1GridData.height = source.readString();
            return m_Tab1GridData;    
        }    
        public Tab1GridData[] newArray(int size) {    
            return new Tab1GridData[size];    
        }    
    };    
}
