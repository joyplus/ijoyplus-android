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
	public DownloadInfo(int compeleteSize,int fileSize,String prod_id,String my_index,String url,String urlposter,String my_name,String download_state)
	{
		this.compeleteSize = compeleteSize;
		this.fileSize = fileSize;
		this.prod_id = prod_id;
		this.my_index = my_index;
		this.url = url;
		this.urlposter = urlposter;
		this.my_name = my_name;
		this.download_state = download_state;
	}
	
	public DownloadInfo() {
	
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
	public String getProdId()
	{
		return prod_id;
	}
	public void setProdId(String prod_id)
	{
		this.prod_id = prod_id;
	}
	public String getIndex()
	{
		return my_index;
	}
	public void setIndex(String my_index)
	{
		this.my_index = my_index;
	}
	public String getPoster()
	{
		return urlposter;
	}
	public void setPoster(String urlposter)
	{
		this.urlposter = urlposter;
	}
	public String getName()
	{
		return my_name;
	}
	public void setName(String name)
	{
		this.my_name = name;
	}
	public String getState()
	{
		return download_state;
	}
	public void setState(String state)
	{
		this.download_state = state;
	}
	
	@Override
	public String toString() {
		/*return "DownloadInfo [threadId=" + threadId + ", startPos=" + startPos
				+ ", endPos=" + endPos + ", compeleteSize=" + compeleteSize
				+ "]";*/
		return "DownloadInfo [compeleteSize=" + compeleteSize + ", fileSize=" + fileSize
				+ ", prod_id=" + prod_id + ",index=" + my_index+",url=" + url+
				",urlposter="+urlposter+",my_name="+my_name+",download_state="+download_state
				+ "]";
	}
}