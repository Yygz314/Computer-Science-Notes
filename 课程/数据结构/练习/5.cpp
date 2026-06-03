#include<iostream>
#include<cstdio>
#include<cstdlib>
#include<cctype>
#include<string>
using namespace std;

#define OK 1
#define ERROR 0
#define OVERFLOW -1
#define STACK_INIT_SIZE 100
#define STACKINCREMENT 10

typedef int Status;
typedef int ElemType;
typedef char SElemType;

// 操作数栈（整数）
typedef struct {
    ElemType *base;
    ElemType *top;
    int stacksize;
} IntStack;

// 运算符栈（字符）
typedef struct {
    SElemType *base;
    SElemType *top;
    int stacksize;
} OpStack;

// 获取运算符栈顶元素
Status GetTop(OpStack &S, SElemType &e) {
    if (S.top == S.base) return ERROR;
    e = *(S.top - 1);
    return OK;
}

// 获取操作数栈顶元素
Status GetTop(IntStack &S, ElemType &e) {
    if (S.top == S.base) return ERROR;
    e = *(S.top - 1);
    return OK;
}

// 初始化运算符栈
Status InitStack(OpStack &S) {
    S.base = (SElemType *)malloc(STACK_INIT_SIZE * sizeof(SElemType));
    if (!S.base) exit(OVERFLOW);
    S.top = S.base;
    S.stacksize = STACK_INIT_SIZE;
    return OK;
}

// 初始化操作数栈
Status InitStack(IntStack &S) {
    S.base = (ElemType *)malloc(STACK_INIT_SIZE * sizeof(ElemType));
    if (!S.base) exit(OVERFLOW);
    S.top = S.base;
    S.stacksize = STACK_INIT_SIZE;
    return OK;
}

// 运算符入栈
Status Push(OpStack &S, SElemType e) {
    if (S.top - S.base >= S.stacksize) {
        S.base = (SElemType *)realloc(S.base, (S.stacksize + STACKINCREMENT) * sizeof(SElemType));
        if (!S.base) exit(OVERFLOW);
        S.top = S.base + S.stacksize;
        S.stacksize += STACKINCREMENT;
    }
    *S.top++ = e;
    return OK;
}

// 操作数入栈
Status Push(IntStack &S, ElemType e) {
    if (S.top - S.base >= S.stacksize) {
        S.base = (ElemType *)realloc(S.base, (S.stacksize + STACKINCREMENT) * sizeof(ElemType));
        if (!S.base) exit(OVERFLOW);
        S.top = S.base + S.stacksize;
        S.stacksize += STACKINCREMENT;
    }
    *S.top++ = e;
    return OK;
}

// 运算符出栈
Status Pop(OpStack &S, SElemType &e) {
    if (S.top == S.base) return ERROR;
    e = *--S.top;
    return OK;
}

// 操作数出栈
Status Pop(IntStack &S, ElemType &e) {
    if (S.top == S.base) return ERROR;
    e = *--S.top;
    return OK;
}

// 运算符优先级映射
int opIndex(char op) {
    switch (op) {
        case '+': return 0;
        case '-': return 1;
        case '*': return 2;
        case '/': return 3;
        case '(': return 4;
        case ')': return 5;
        case '#': return 6;
        default: return -1;
    }
}

// 获取运算符优先级
char Precede(char a, char b) {
    // 优先级表 [行：栈顶运算符] [列：当前运算符]
    char precede[7][7] = {
        { '>', '>', '<', '<', '<', '>', '>' }, // +
        { '>', '>', '<', '<', '<', '>', '>' }, // -
        { '>', '>', '>', '>', '<', '>', '>' }, // *
        { '>', '>', '>', '>', '<', '>', '>' }, // /
        { '<', '<', '<', '<', '<', '=', '/' }, // (
        { '>', '>', '>', '>', '/', '>', '>' }, // )
        { '<', '<', '<', '<', '<', '/', '=' }  // #
    };
    int i = opIndex(a);
    int j = opIndex(b);
    if (i == -1 || j == -1) return '?';
    return precede[i][j];
}

// 执行算术运算
ElemType Operate(ElemType a, char theta, ElemType b) {
    switch (theta) {
        case '+': return a + b;
        case '-': return a - b;
        case '*': return a * b;
        case '/': 
            if (b == 0) {
                exit(OVERFLOW);
            }
            return a / b;
        default:
            exit(OVERFLOW);
    }
}

// 表达式求值
ElemType EvaluateExpression(string s) {
    OpStack OPTR;   
    IntStack OPND;  
    InitStack(OPTR);
    Push(OPTR, '#'); 
    
    InitStack(OPND);
    
    int i = 0;
    SElemType c = s[i++];
    SElemType topOp, x, theta;
    ElemType a, b;
    
    GetTop(OPTR, topOp);
    
    while (c != '#' || topOp != '#') {
        if (isdigit(c)) { 
            ElemType num = 0;
            while (i <= s.length() && isdigit(c)) {
                num = num * 10 + (c - '0');
                c = s[i++];
            }
            Push(OPND, num);
        } 
        else if (isspace(c)) { 
            c = s[i++];
        }
        else { 
            GetTop(OPTR, topOp);
            char relation = Precede(topOp, c);
            
            switch (relation) {
                case '<': 
                    Push(OPTR, c);
                    c = s[i++];
                    break;
                    
                case '=': 
                    Pop(OPTR, x); 
                    c = s[i++];
                    break;
                    
                case '>': 
                    Pop(OPTR, theta); 
                    Pop(OPND, b);     
                    Pop(OPND, a);     
                    Push(OPND, Operate(a, theta, b)); 
                    break;
            }
        }
        
        GetTop(OPTR, topOp);
    } 
    ElemType result;
    GetTop(OPND, result);
    free(OPTR.base);
    free(OPND.base);
    
    return result;
}

int main() {
    string s;
    cout << "请输入表达式: ";
    getline(cin, s); 
    
    if (s.empty() || s.back() != '#') {
        s += '#';
    }
    
    ElemType result = EvaluateExpression(s);
    cout << "计算结果: " << result << endl;
    return 0;
}