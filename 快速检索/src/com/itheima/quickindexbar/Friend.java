package com.itheima.quickindexbar;

public class Friend implements Comparable<Friend> {
	public String name;
	public String pinyin;

	public Friend(String name) {
		this.name = name;
		//每个拼音都是固定的,构造方法中直接初始化pinyin
		pinyin = PinYinUtils.getPinYin(name);
	}
	
	@Override
	public int compareTo(Friend another) {
		return pinyin.compareTo(another.pinyin);
	}
	
}
