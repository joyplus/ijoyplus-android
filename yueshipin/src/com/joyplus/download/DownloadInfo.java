package com.joyplus.download;

/**
 * 创建一个下载信息的实体类
 */
public class DownloadInfo {
	private int compeleteSize;// 完成度
	private int fileSize;//文件的大小
	private String prod_id;//视频的id号
	private String my_index;//如果为电视剧或者节目时index为正数
	private String url;// 下载器网络标识
	private String urlposter;//海报
	private String my_name;
	private String download_state;
	private String file_path;
	private String localfile;//存放视频路径
	
	public DownloadInfo(int compeleteSize,int fileSize,String prod_id,String my_index,String url,String urlposter,String my_name,String download_state,String file_path)
	{
		this.compeleteSize = compeleteSize;
		this.fileSize = fileSize;
		this.prod_id = prod_id;
		this.my_index = my_index;
		this.url = url;
		this.urlposter = urlposter;
		this.my_name = my_name;
		this.download_state = download_state;
		this.file_path = file_path;
	}
	
	public DownloadInfo() {
	
	}
	
	public int getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(int compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getProd_id() {
		return prod_id;
	}

	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}

	public String getMy_index() {
		return my_index;
	}

	public void setMy_index(String my_index) {
		this.my_index = my_index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlposter() {
		return urlposter;
	}

	public void setUrlposter(String urlposter) {
		this.urlposter = urlposter;
	}

	public String getMy_name() {
		return my_name;
	}

	public void setMy_name(String my_name) {
		this.my_name = my_name;
	}

	public String getDownload_state() {
		return download_state;
	}

	public void setDownload_state(String download_state) {
		this.download_state = download_state;
	}
	public String  getLocalpath(){
		return localfile;
	}
	
	public void setFilePath(String file_path)
	{
		this.file_path = file_path;
	}
	
	public String getFilePath()
	{
		return file_path;
	}
	
	@Override
	public String toString() {
		return "DownloadInfo [compeleteSize=" + compeleteSize + ", fileSize=" + fileSize
				+ ", prod_id=" + prod_id + ",index=" + my_index+",url=" + url+
				",urlposter="+urlposter+",my_name="+my_name+",download_state="+download_state
				+",file_path="+file_path+ "]";
	}
}