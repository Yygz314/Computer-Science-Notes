# 基于Java Swing图形界面编程的简易计算器项目开发

### 1. 整体思路

> - 定义工具类`CalcUtil` ，负责输入校验和计算逻辑
> - 定义界面类`CalculatorUI`，负责窗口、按钮、输入框、鼠标/键盘交互

### 2.工具类 `com.util.CalcUtil`

#### （1）功能定位

> - **输入校验**：判断用户输入的表达式是否合法（禁止连续符号、非法字符、错误小数点）
> - **表达式计算**：实现**先乘除后加减**，支持**负数 / 小数**，用`BigDecimal`解决浮点数精度问题

#### （2）技术类型

> - **栈**：存储数字和运算符，实现**运算符优先级计算**
> - **BigDecimal**：替代 double，彻底解决`0.1*3=0.30000004`精度误差
> - **正则表达式**：快速校验输入合法性

#### （3）实现过程

> **步骤一：创建类+定义变量**

```Java
package com.util; // 归属工具包

import java.math.BigDecimal;  // 高精度计算
import java.math.RoundingMode;// 四舍五入
import java.util.Stack;       // 栈结构，实现优先级计算

public class CalcUtil {
    // 常量：除法保留10位小数，防止除不尽报错
    private static final int SCALE = 10;
```

> 步骤二：输入合法性校验方法

```Java
// 功能：校验表达式是否合法，返回true/false
public static boolean checkValid(String exp) {
    // 1. 空表达式直接非法
    if (exp.isEmpty()) return false;
    // 2. 仅允许：首位负号 + 数字/小数点/+-*/
    if (!exp.matches("^-?[0-9+\\-*/.]*")) return false;
    // 3. 禁止连续运算符（如 1++2、3*-4）
    if (exp.matches(".*[+\\-*/]{2,}.*")) return false;
    // 4. 禁止除首位外，以运算符结尾（如 123+）
    if (exp.substring(1).matches(".*[+\\-*/]$")) return false;
    // 5. 禁止连续小数点（如 5..6）
    if (exp.matches(".*[.]{2,}.*")) return false;
    // 所有校验通过，返回合法
    return true;
}
```

> 步骤 3：表达式计算核心方法

```Java
// 功能：计算合法表达式，返回结果字符串
public static String calculate(String exp) {
    try {
        // 1. 创建两个栈：数字栈（存BigDecimal）、运算符栈（存+*-/）
        Stack<BigDecimal> nums = new Stack<>();
        Stack<Character> ops = new Stack<>();
        // 去掉表达式中的空格（容错处理）
        exp = exp.replaceAll(" ", "");

        // 2. 遍历表达式的每一个字符
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);

            // 🔥 处理负数（如 -6*5，判断：首位是- 或 运算符后是-）
            if (c == '-' && (i == 0 || "+-*/".contains(exp.charAt(i - 1) + ""))) {
                StringBuilder sb = new StringBuilder("-");
                i++;
                // 拼接完整负数（含小数点）
                while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
                    sb.append(exp.charAt(i++));
                }
                nums.push(new BigDecimal(sb.toString()));
                i--;
            }
            // 3. 处理普通数字/小数点
            else if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                // 拼接完整数字（如 123、3.14）
                while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
                    sb.append(exp.charAt(i++));
                }
                nums.push(new BigDecimal(sb.toString()));
                i--;
            }
            // 4. 处理运算符（+*-/），按优先级计算
            else {
                // 如果当前运算符优先级 ≤ 栈顶运算符，先计算栈内
                while (!ops.isEmpty() && priority(ops.peek()) >= priority(c)) {
                    calc(nums, ops);
                }
                ops.push(c);
            }
        }

        // 5. 计算栈中剩余的所有运算
        while (!ops.isEmpty()) calc(nums, ops);
        BigDecimal res = nums.pop();

        // 6. 格式化结果：去掉末尾多余的0
        res = res.stripTrailingZeros();
        return res.toPlainString();
    } catch (Exception e) {
        // 异常返回：运算错误
        return "运算错误";
    }
}
```

> 步骤 4：辅助方法（优先级 + 单次计算）

```Java
// 功能：定义运算符优先级（乘除=1，加减=0）
private static int priority(char op) {
    return op == '*' || op == '/' ? 1 : 0;
}

// 功能：单次四则运算（取出两个数字+一个运算符，计算后压回栈）
private static void calc(Stack<BigDecimal> nums, Stack<Character> ops) {
    BigDecimal b = nums.pop(); // 后出栈的是第二个数
    BigDecimal a = nums.pop(); // 先出栈的是第一个数
    char op = ops.pop();       // 取出运算符
    BigDecimal res;

    // 高精度四则运算
    switch (op) {
        case '+': res = a.add(b); break;
        case '-': res = a.subtract(b); break;
        case '*': res = a.multiply(b); break;
        case '/': res = a.divide(b, SCALE, RoundingMode.HALF_UP); break;
        default: throw new IllegalArgumentException();
    }
    // 计算结果压回数字栈
    nums.push(res);
}
}
```

### 3.界面类 `com.frm.CalculatorUI`

#### （1）功能定位

> - **UI 布局**：自适应窗口（放大不变形），包含**输入框 + CE 清空按钮 + 16 个标准按键**
>
> - **交互逻辑**：鼠标点击输入、键盘输入、`=`/Enter 计算、续算、原式不消失
>
> - **兼容优化**：`Shift+=`输入加号不触发运算，拦截非法键盘输入

#### （2）技术类型

> - **BorderLayout**：主布局，分「顶部（输入框）+ 中间（按钮）」
> - **GridLayout**：按钮布局，4×4 网格均匀排列
> - **绝对禁止 null 布局**：解决窗口放大错乱问题

#### （3）实现过程

> 步骤 1：创建类 + 定义全局变量

```Java
package com.frm; // 归属界面包

// 导入Swing界面包 + 事件监听包
import java.awt.BorderLayout;
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

// 导入工具类（调用计算+校验）
import com.util.CalcUtil;

public class CalculatorUI extends JFrame { // 继承JFrame，成为窗口
    private JTextField txtInput; // 输入框（全局变量，所有方法都能调用）
    // 16个标准按键（固定顺序：4行4列）
    private String[] keys = {"7","8","9","/","4","5","6","*","1","2","3","-","0",".","=","+"};
    // 四则运算符（方便后续判断）
    private final String[] OPS = {"+", "-", "*", "/"};
```

> 步骤 2：窗口初始化 + 布局搭建

```Java
// 构造方法：窗口一创建就执行（界面初始化）
public CalculatorUI() {
    // 1. 窗口基础设置
    setTitle("标准计算器");       // 标题
    setDefaultCloseOperation(EXIT_ON_CLOSE); // 关闭窗口退出程序
    setBounds(100, 100, 380, 400); // 初始位置+大小
    setMinimumSize(new java.awt.Dimension(300, 350)); // 最小窗口，防止变形

    // 2. 主面板：BorderLayout自适应布局（带间距）
    JPanel contentPane = new JPanel(new BorderLayout(10, 10));
    contentPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // 内边距
    setContentPane(contentPane);

    // 3. 顶部面板：输入框 + CE清空按钮
    JPanel topPanel = new JPanel(new BorderLayout(5, 0));
    // 输入框
    txtInput = new JTextField();
    txtInput.setFont(new Font("微软雅黑", Font.PLAIN, 22)); // 字体
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
```

> 步骤 3：键盘监听（核心优化）

```Java
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
            if ((e.getKeyCode() == KeyEvent.VK_EQUALS && !e.isShiftDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
                calculateResult(); // 调用统一计算方法
            }
        }
    });
}
```

> 步骤 4：统一计算方法（公共逻辑）

```Java
// 公共计算方法：按钮=、键盘=、Enter 都调用它
private void calculateResult() {
    String current = txtInput.getText().trim(); // 获取输入框内容
    // 禁止：空内容/已计算完成 重复计算
    if (current.isEmpty() || current.contains("=")) return;
    // 调用工具类校验合法性
    if (!CalcUtil.checkValid(current)) {
        txtInput.setText("输入非法");
        return;
    }
    // 调用工具类计算结果
    String result = CalcUtil.calculate(current);
    // 核心：保留原式，拼接 表达式=结果
    txtInput.setText(current + " = " + result);
}
```

> 步骤 5：鼠标按钮点击监听

```Java
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
```

> 步骤 6：程序入口

```Java
// 主方法：运行程序
public static void main(String[] args) {
    // Swing线程安全启动窗口
    javax.swing.SwingUtilities.invokeLater(() -> new CalculatorUI().setVisible(true));
}
}
```

### 4. 项目逻辑

> **输入**：鼠标点击 / 键盘输入 → 输入框追加内容
>
> **校验**：按`=`/Enter → 调用`CalcUtil.checkValid()`校验合法性
>
> **计算**：合法 → 调用`CalcUtil.calculate()`高精度计算
>
> **显示**：输入框显示 `原式 = 结果`（原式不消失）
>
> **续算**：按运算符 → 自动提取结果，拼接运算符
>
> **清空**：点击 CE → 一键重置输入框
>
> **容错**：非法输入 → 显示「输入非法」，异常→显示「运算错误」

### 5. 项目展示

![image-20260328153232822](../../../Program Files/typora/Typora/picture/image-20260328153232822.png)











