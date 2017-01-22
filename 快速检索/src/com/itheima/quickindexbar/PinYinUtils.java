package com.itheima.quickindexbar;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

/**
 * 汉字转拼音的工具类,是根据查找xml去找拼音的,会消耗一定的系统资源,不易平凡调用
 * 
 */
public class PinYinUtils {

	public static String getPinYin(String chinese) {
		if (TextUtils.isEmpty(chinese)) {	//汉字为空,返回null
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		//获取jar包的格式对象
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);	//转化称大写的拼音
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);	//设置没有声调

		char[] charArray = chinese.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			//去除空格,遇到空格跳出本次循环
			if (Character.isWhitespace(charArray[i])) {
				continue;
			}
			//判断是否是汉字,因为可能是其他字符,汉字占2个字节,1个字节范围-128-127之间
			if (charArray[i] > 127) {	//可能是汉字
				try {
					//这里返回的是拼音数组,因为可能有多音字的存在,但我们只取第一个常用的音
					String[] hanziArr = PinyinHelper.toHanyuPinyinStringArray(charArray[i], format);
					if (hanziArr != null) {
						//肯定是汉字的情况就拼接
						sb.append(hanziArr[0]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					//异常表示转换不了拼音,可能不是汉字或其他原因
				}
				
			}else {	//肯定不是汉字,是字符也拼接上
				sb.append(charArray[i]);
				
			}
		}
		
		return sb.toString();
	}
}
