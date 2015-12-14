package com.videoserver.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
	private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
	private static File saveFile;
	private static File sendFile;
	
	
	/**文件夹命名格式为  D：/videoFiles/IMEI/date/time
	 * @Param file 目录路径 "D：/videoFiles/IMEI/date"
	 * 用于每一次设备请求发送视频文件时，定位到视频文件
	 * */
	public static File getFileToSave(File file){		
		if(file == null || !file.isDirectory()){
			return null;
		}
		
		List<File> files = getFileList(file);
		if(files == null || files.size() == 0){
			String fileName = DateUtil.getFullTime()+".h264";
			saveFile = new File(file,fileName);
			if(!saveFile.exists()){
				try{
					saveFile.createNewFile();
				}catch(IOException e){
					logger.error("error when creating a file to save video wit "+ file.getPath()+ e.getMessage());
				}
			}
			return saveFile;
		}
		
		//判断时间最近的文件是否小于10M
		saveFile = files.get(0);
		if(saveFile.length() >= (10<<20)){
			saveFile = new File(file,DateUtil.getNowTime()+".h264");
			if(!saveFile.exists()){
				try{
					saveFile.createNewFile();
				}catch(IOException e){
					logger.error("error when creating a file to save video wit "+ file.getPath());
				}
			}
			return saveFile;
		}
		return saveFile;
	}
	
	
	/**没有当天的视频文件则返回null；
	 * 否则返回当天最后一个视频文件的内容
	 * @Param file 是一个目录路径 D：/videoFiles/IMEI/date
	 * */
	public static File getFileToSend(File file){
		if(file == null || !file.isDirectory()){
			return null;
		}
		
		List<File> files = getFileList(file);
		if(files == null || files.size() == 0){
			return null;
		}
		return files.get(0);
	}
	
	/**获取下一个将发送的视频文件，如果没有，则返回空
	 * */
	public static File getNextFileToSend(File file){
		sendFile = file;
		List<File> files = getLaterFileList(new File(file.getParent()));
		if(files == null || files.size() == 0){
			return null;
		}
		return files.get(0);
	}
	
	/**返回所有的文件列表，并按名称由大到小排序*/
	private static List<File> getFileList(File file){
		List<File> files = Arrays.asList(new File(file.toString()).listFiles());		
		if(files == null || files.size() == 0){
			return null;
		}
		
		Collections.sort(files, new Comparator<File>(){
		    @Override
		    public int compare(File o1, File o2) {
		    	return o2.getName().compareTo(o1.getName());
		    }
		});
		return files;
	}
	
	/**返回名称比当前文件大的文件列表*/
	private static List<File> getLaterFileList(File file){
		List<File> files = Arrays.asList(file.listFiles(new FileFilter()));
		if(files == null || files.size() == 0){
			return null;
		}
		
		Collections.sort(files, new Comparator<File>(){
		    @Override
		    public int compare(File o1, File o2) {
		    	return o1.getName().compareTo(o2.getName());
		    }
		});
		return files;
	}
  
	private static class FileFilter implements FilenameFilter{
		public boolean accept(File dir, String name){
			return (name.compareTo(sendFile.getName()) == 1);
		}
	}
	
//	public static void main(String[] args){
//		File file = new File("D:/videoFiles/0001/2015-11-17");
//		System.out.println(getFileToSave(file).toString());
//		System.out.println(getFileToSend(file));
//		System.out.println(getNextFileToSend(new File("D:/videoFiles/0001/2015-11-17/2015-11-17-16_18_53.h264")));
//	}
}
