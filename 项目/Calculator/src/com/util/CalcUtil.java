package com.util;

import java.math.BigDecimal;	//高精度计算
import java.math.RoundingMode;	//四舍五入
import java.util.Stack;	//栈

public class CalcUtil {
	//定义常量。除法除不尽保留10位小数
	private static final int SCALE = 10;
	
	//校验表达式是否合法
	public static boolean checkValid(String exp) {
		//空表达式
		if(exp.isEmpty()) return false;
		//仅允许数字，小数点，四目运算符，注意首位可以为负数
		if(!exp.matches("^-?[0-9+\\-*/.]*")) return false;
        //禁止连续运算符（如 1++2、3*-4）
        if (exp.matches(".*[+\\-*/]{2,}.*")) return false;
        //禁止除首位外，其他位置以运算符结尾/开头
        if (exp.substring(1).matches(".*[+\\-*/]$")) return false;
        //禁止连续小数点（如5..6）
        if (exp.matches(".*[.]{2,}.*")) return false;
        //禁止无意义小数点（如 .12、12.）
        if (exp.matches(".*[^0-9][.]|[.][^0-9].*")) return false;
        return true;
	}
	
	//计算合法表达式
	public static String calculate(String exp) {
		try {
			//1.创建两个栈（数字栈和运算符栈）
			Stack<BigDecimal> nums = new Stack<>();
			Stack<Character> ops = new Stack<>();
			//删去表达式的空格
			exp = exp.replaceAll("","");
			
			//2.遍历表达式字符
			for (int i = 0;i<exp.length();i++) {
				char c = exp.charAt(i);
				//处理负数(首位是负号或运算符后是负号)
				if(c == '-' && (i == 0 || "+-*/".contains(exp.charAt(i-1)+""))) {
					StringBuilder sb = new StringBuilder('-');
					i++;
					//拼接完整负数
					while(i<exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
						sb.append(exp.charAt(i++));
					}
					nums.push(new BigDecimal(sb.toString()));
					i--;
				}
				//处理普通数字/小数点
				else if(Character.isDigit(c) || c == '.') {
					StringBuilder sb = new StringBuilder();
	                while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
	                    sb.append(exp.charAt(i++));
	                }
	                nums.push(new BigDecimal(sb.toString()));
	                i--;
				}
				//处理运算符
				else {
					//如果当前运算符优先级小于等于栈顶运算符，先计算栈内
					while(!ops.isEmpty() && priority(ops.peek())>=priority(c)) {
						calc(nums,ops);
					}
					ops.push(c);
				}
			}
			
			//3.计算栈中剩余运算
			while(!ops.isEmpty()) calc(nums,ops);
			BigDecimal res = nums.pop();
			
			return res.toPlainString();
		} catch(Exception e) {
			return  "运算错误";
		}
	}
	
	//定义运算符优先级
	private static int priority(char op) {
		return op == '*' || op == '/' ? 1 : 0;
	}
	
	//定义单次四则运算（去除两个数字，一个运算符，计算后压回栈）
	private static void calc(Stack<BigDecimal> nums,Stack<Character> ops) {
		BigDecimal b = nums.pop();
		BigDecimal a = nums.pop();
		char op = ops.pop();
		BigDecimal res;
		
		switch(op){
			case '+' : res = a.add(b); break;
			case '-' : res = a.subtract(b); break;
			case '*' : res = a.multiply(b); break;
			case '/' : res = a.divide(b,SCALE,RoundingMode.HALF_UP); break;
			default: throw new IllegalArgumentException();
		}
		nums.push(res);
	}

}




















