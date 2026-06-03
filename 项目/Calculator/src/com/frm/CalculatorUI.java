package com.frm;

// 导入Swing界面包 + 事件监听包
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

//导入工具类（调用计算+校验）
import com.util.CalcUtil;

public class CalculatorUI extends JFrame {
	
	private JTextField txtInput; // 输入框（全局变量，所有方法都能调用）
	// 16个标准按键（0-9、+-*/、.、=）
	private String[] keys = {
		"7","8","9","/",
		"4","5","6","*",
		"1","2","3","-",
		"0",".","=","+"
	};
	//四则运算符号
	private final String[] OPS = {"+", "-", "*", "/"};

	/**
	 * Create the frame.
	 */
	public CalculatorUI() {
		//1.窗口基础设置
		setTitle("简易计算器");	//标题
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//关闭窗口退出程序
		setBounds(100, 100, 450, 300);	//初始位置+大小
		setMinimumSize(new java.awt.Dimension(300,350));	//最小窗口，防止变形
		
		//2.主面板 BorderLayout自适应布局（带间距）
		JPanel contentPane = new JPanel(new BorderLayout(10,10));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));	//内边距
		setContentPane(contentPane);
		
		//3.顶部面板 输入框+CE清空按钮
		JPanel topPanel = new JPanel(new BorderLayout(5 , 0));
		//输入框
		txtInput = new JTextField();
		txtInput.setFont(new Font("微软雅黑",Font.PLAIN,22));//字体
	    txtInput.setHorizontalAlignment(JTextField.RIGHT);     // 文字右对齐
	    txtInput.setEditable(true); // 开启键盘输入
	    topPanel.add(txtInput, BorderLayout.CENTER); // 输入框占满左侧
	    // CE清空按钮
	    JButton btnClear = new JButton("CE");
	    btnClear.setFont(new Font("微软雅黑", Font.PLAIN, 16));
	    btnClear.setPreferredSize(new java.awt.Dimension(60, 50)); // 固定大小
	    btnClear.addActionListener(e -> txtInput.setText("")); // 点击清空输入框
	    topPanel.add(btnClear, BorderLayout.EAST); // 按钮固定右侧
	    contentPane.add(topPanel, BorderLayout.NORTH); // 顶部面板放入主窗口
	    
	    // 4. 按键面板：4×4网格布局
	    JPanel keyPanel = new JPanel(new GridLayout(4, 4, 8, 8)); // 带间距
	    // 循环创建16个按钮
	    for (String text : keys) {
	        JButton btn = new JButton(text);
	        btn.setFont(new Font("微软雅黑", Font.BOLD, 20));
	        btn.addActionListener(new BtnListener()); // 绑定鼠标点击事件
	        keyPanel.add(btn);
	    }
	    contentPane.add(keyPanel, BorderLayout.CENTER); // 按钮面板放入中间
	    
	    // 键盘监听：支持键盘输入、拦截非法字符、区分Shift+=
	    txtInput.addKeyListener(new KeyAdapter() {
	        // 1. 拦截非法字符输入
	        @Override
	        public void keyTyped(KeyEvent e) {
	            char c = e.getKeyChar();
	            // 禁止直接输入=号（防止污染输入框）
	            if (c == '=') { e.consume(); return; }
	            // 仅允许：数字、小数点、+-*/、退格键
	            if (!Character.isDigit(c) && c != '.' && c != '+' && c != '-' && c != '*' && c != '/' && c != KeyEvent.VK_BACK_SPACE) {
	                e.consume(); // 拦截非法字符
	            }
	        }

	        // 2. 按键按下：纯=键/Enter触发运算，Shift+=输加号不触发
	        @Override
			public void keyPressed(KeyEvent e) {
				// 纯按=键/按Enter键触发运算；Shift+=仅输入加号，不触发
				if ((e.getKeyCode() == KeyEvent.VK_EQUALS && !e.isShiftDown()) 
						|| e.getKeyCode() == KeyEvent.VK_ENTER) {
					calculateResult(); // 执行计算
				}
			}
		});
	}


	// 公共计算方法：保留原式，右侧拼接=结果（所有运算触发统一调用）
	private void calculateResult() {
		String current = txtInput.getText().trim();
		// 空内容/已计算完成禁止重复运算
		if (current.isEmpty() || current.contains("=")) return;
		// 输入合法性检测
		if (!CalcUtil.checkValid(current)) {
			txtInput.setText("输入非法");
			return;
		}
		// 高精度计算
		String result = CalcUtil.calculate(current);
		txtInput.setText(current + " = " + result);
	}

	// 鼠标点击事件：处理按钮输入、续算、计算
	private class BtnListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        String key = e.getActionCommand(); // 获取点击的按钮文字
	        String current = txtInput.getText().trim();

	        // 1. 点击=按钮：调用计算方法
	        if (key.equals("=")) { calculateResult(); }
	        // 2. 点击运算符（+-*/）：实现续算功能
	        else if (isOperator(key)) {
	            // 如果已计算完成（含=），提取结果续算
	            if (current.contains("=")) {
	                // 非法结果不续算
	                if (current.contains("输入非法") || current.contains("运算错误")) return;
	                String lastResult = current.split("=")[1].trim();
	                txtInput.setText(lastResult + key);
	            } else {
	                // 未计算，直接追加运算符
	                txtInput.setText(current + key);
	            }
	        }
	        // 3. 点击数字/小数点：正常输入
	        else {
	            // 已计算/非法，清空重新输入
	            if (current.contains("=") || current.equals("输入非法") || current.equals("运算错误")) {
	                txtInput.setText(key);
	            } else {
	                // 未计算，追加数字
	                txtInput.setText(current + key);
	            }
	        }
	    }

	    // 辅助方法：判断是否为运算符
	    private boolean isOperator(String key) {
	        for (String op : OPS) if (op.equals(key)) return true;
	        return false;
	    }
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(()->{
			new CalculatorUI().setVisible(true);
		});
	}
}










